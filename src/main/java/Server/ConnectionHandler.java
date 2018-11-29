package Server;

import Shared.Models.*;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static Shared.Models.Actions.*;
import static Shared.Models.Actions.ACTION_RATE_UBER;
import static Shared.Models.Status.FAILURE;
import static Shared.Models.Status.SUCCESS;

public class ConnectionHandler implements Runnable {
    private final Socket mSocket;
    private PrintWriter mSender;
    private BufferedReader mReceiver;

    public ConnectionHandler(Socket socket) {
        mSocket = socket;
    }

    public void run() {

        try {
            mSender = new PrintWriter(mSocket.getOutputStream(), true);
            mReceiver = new BufferedReader( new InputStreamReader( mSocket.getInputStream() ) );

            // Imprimir a primeira mensagem
            //out.println(echoMessage);

            String clientRequest;
            Gson gson = new Gson();
            while ((clientRequest = mReceiver.readLine()) != null) {

                Request req = gson.fromJson(clientRequest, Request.class);
                String reqAction = req.getAction();
                String reqArgs = req.getArgs();

                if (reqAction.equals(ACTION_SIGNUP)) {
                    User newUser = gson.fromJson(reqArgs, User.class);
                    boolean isSignedUp = handleSignup(newUser);

                    // Send answer back to the client
                    Response res;
                    if (isSignedUp) {
                        res = new Response(SUCCESS);
                    } else {
                        res = new Response(FAILURE);
                    }

                    // Serialize response
                    String resJson = gson.toJson(res);

                    // Send response to client
                    mSender.println(resJson);

                } else if (reqAction.equals(ACTION_LOGIN)) {

                    // Login the user
                    Credentials credentials = gson.fromJson(reqArgs, Credentials.class);
                    User user = handleLogin(credentials);

                    // Send answer back to the client
                    Response res;
                    if (user != null) {
                        String userJson = gson.toJson(user);
                        res = new Response(SUCCESS, userJson);
                    } else {
                        res = new Response(FAILURE);
                    }

                    // Serialize response
                    String resJson = gson.toJson(res);

                    // Send response to client
                    mSender.println(resJson);

                } else if (reqAction.equals(ACTION_CALL_UBER)) {

                    // Signup the user
                    Ride ride = gson.fromJson(reqArgs, Ride.class);
                    ride = handleCallUber(ride);

                    // Send answer back to the client
                    Response res;
                    if (ride != null) {
                        String rideJson = gson.toJson(ride);
                        res = new Response(ACTION_SIGNAL_CLIENT_END_TRIP, rideJson);

                        // Serialize response
                        String resJson = gson.toJson(res);

                        // Send response to client
                        mSender.println(resJson);
                    } else {
                        System.exit(-1);
                        System.out.println("Failed to signal end of trip to the client. Something went horribly wrong!");
                    }

                } else if (reqAction.equals(ACTION_ACCEPT_RIDE)) {

                    // Deserialize ride
                    Ride updatedRide = gson.fromJson(reqArgs, Ride.class);

                    RideSystem rideSystem = RideSystem.getInstance();

                    // Make sure some other driver didn't already accept it
                    Ride rideInSystem = rideSystem.getRideByClient(updatedRide.getClientUsername());

                    // Meanwhile, the client's handle finds and removes the ride. So it should be no longer there if someone else took it
                    if (rideInSystem != null) {

                        // Send failure response back
                        Response res = new Response(FAILURE, "");
                        String resJson = gson.toJson(res);
                        mSender.println(resJson);
                    } else {

                        // Add it to the system so the client's handler can find it
                        rideSystem.addRide(updatedRide);

                        // Send response back
                        Response res = new Response(SUCCESS, "");
                        String resJson = gson.toJson(res);
                        mSender.println(resJson);
                    }

                } else if (reqAction.equals(ACTION_SIGNAL_SERVER_END_TRIP)) {
                    // Deserialize ride
                    Ride ride = gson.fromJson(reqArgs, Ride.class);

                    RideSystem rideSystem = RideSystem.getInstance();
                    rideSystem.getRideByClient(ride.getClientUsername()).setComplete(true);
                    Response res = new Response(SUCCESS, "");
                    String resJson = gson.toJson(res);
                    mSender.println(resJson);

                } else if (reqAction.equals(ACTION_END_RIDE)) { //TODO: Have driver send this
                    Response res = new Response(SUCCESS, "");
                    String resJson = gson.toJson(res);
                    mSender.println(resJson);

                } else if (reqAction.equals(ACTION_RATE_UBER)) {
                    // Deserialize ride
                    Ride incompleteInfoRide = gson.fromJson(reqArgs, Ride.class);

                    // Get Ride object with more complete info
                    RideSystem rideSystem = RideSystem.getInstance();
                    Ride ride = rideSystem.getRideByDriver(incompleteInfoRide.getDriverUsername());

                    // Add rating
                    ride.setRating(incompleteInfoRide.getRating());

                    // Add trip to history and remove from active
                    rideSystem.removeRide(ride);
                    UberServer.mAuth.saveUserRide(ride.getDriverUsername(), ride);
                    UberServer.mAuth.saveUserRide(ride.getClientUsername(), ride);

                    Response res = new Response(SUCCESS, "");
                    String resJson = gson.toJson(res);
                    mSender.println(resJson);
                }
                System.out.println(clientRequest);
            }
        } catch (IOException ioe) {
            System.out.println("Error handling client!");
            System.exit(-1);
        }
    }

    private boolean handleSignup(User newUser) {
        String username = newUser.getUsername();
        String password = newUser.getPassword();
        String userType = newUser.getUserType();

        return UberServer.mAuth.signup(username, password, userType);
    }

    private User handleLogin(Credentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        return UberServer.mAuth.login(username, password);
    }

    private Ride handleCallUber(Ride ride) {

        // Tell drivers about the trip
        MulticastServer driverFinder = new MulticastServer();
        driverFinder.notifyDrivers(ride);

        // Wait for a driver to accept the ride
        RideSystem rideSystem = RideSystem.getInstance();
        Ride updatedRide = null;
        boolean isRideOver = false;
        boolean isClientInformed = false;
        String clientUsername = ride.getClientUsername();
        do {

            // Try to get updated ride
            updatedRide = rideSystem.getRideByClient(clientUsername);
            if (updatedRide != null) {
                // When the ride is given a driver, inform the client
                if (!isClientInformed) {

                    // Inform client
                    Gson gson = new Gson();
                    String rideJson = gson.toJson(updatedRide);
                    Response res = new Response(SUCCESS, rideJson);
                    String resJson = gson.toJson(res);
                    mSender.println(resJson);

                    // Signal that the client has been informed already
                    isClientInformed = true;
                }
                isRideOver = updatedRide.isComplete();
            }
        } while(!isRideOver);

        return updatedRide;
    }
}
