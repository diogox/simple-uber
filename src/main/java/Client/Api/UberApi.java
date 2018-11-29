package Client.Api;

import Client.Utils.ThreadChannel;
import Shared.Models.*;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static Shared.Models.Actions.*;
import static Shared.Models.Status.FAILURE;
import static Shared.Models.Status.SUCCESS;

public class UberApi implements Runnable {
    private static final int SERVER_PORT = 3322;
    private final ThreadChannel mChannel;
    private Socket mServer;
    private BufferedReader mReceiver;
    private PrintWriter mSender;

    public UberApi(ThreadChannel channel) {
        mChannel = channel;
    }

    public void run() {
        try {
            // Establish connection
            mServer = new Socket("localhost", SERVER_PORT);

            // Get message receiver
            mReceiver = new BufferedReader(new InputStreamReader(mServer.getInputStream()));

            // Get message sender
            mSender = new PrintWriter(mServer.getOutputStream(), true);

            //System.out.println("server says:" + mReceiver.readLine());

            //mSender.println(userInput);

            String action;
            do {
                ThreadChannel.Data data = mChannel.receiveOnOtherThread();
                action = data.getAction();
                List<String> args = data.getArgs();
                if (action.equals(ACTION_LOGIN)) {
                    User user = onLogin(args);

                    // Tell the main branch if it was successful
                    List<String> loginArgs = new ArrayList<String>();
                    if(user != null) {
                        loginArgs.add(user.getUserType());
                        mChannel.sendToMainThread(SUCCESS, loginArgs);
                    } else {
                        mChannel.sendToMainThread(FAILURE, loginArgs);
                    }

                } else if (action.equals(ACTION_SIGNUP)) {
                    boolean isSignedUp = onSignup(args);

                    // Tell the main branch if it was successful
                    List<String> signupArgs = new ArrayList<String>();
                    if(isSignedUp) {
                        mChannel.sendToMainThread(SUCCESS, signupArgs);
                    } else {
                        mChannel.sendToMainThread(FAILURE, signupArgs);
                    }

                } else if (action.equals(ACTION_CALL_UBER)) {
                    Ride ride = onUberCall(args);

                    List<String> callUberArgs = new ArrayList<String>();
                    if(ride != null) {
                        callUberArgs.add(ride.getDriverUsername());
                        mChannel.sendToMainThread(SUCCESS, callUberArgs);
                    } else {
                        mChannel.sendToMainThread(FAILURE, callUberArgs);
                        System.out.println("Failed to assign driver. Something went horribly wrong!");
                        System.exit(-1);
                    }

                } else if (action.equals(ACTION_ACCEPT_RIDE)) {
                    boolean rideWasAvailable = onAcceptRide(args);

                    if(rideWasAvailable) {
                        mChannel.sendToMainThread(SUCCESS, null);
                    } else {
                        mChannel.sendToMainThread(FAILURE, null);
                    }

                } else if (action.equals(ACTION_SIGNAL_CLIENT_END_TRIP)) {
                    // Wait for server to signal end of ride
                    mReceiver.readLine();
                    mChannel.sendToMainThread(SUCCESS, null);

                } else if (action.equals(ACTION_SIGNAL_SERVER_END_TRIP)) {
                    onSignalEndTrip(args);
                    mChannel.sendToMainThread(SUCCESS, null);

                } else if (action.equals(ACTION_END_RIDE)) {
                    List<String> emptyArgs = new ArrayList<String>();
                    mChannel.sendToMainThread(SUCCESS, emptyArgs);

                } else if (action.equals(ACTION_RATE_UBER)) {
                    onRateUber(args);
                    mChannel.sendToMainThread(SUCCESS, null);
                } else if (action.equals(ACTION_QUIT)) {
                    System.out.println("Quitting!");
                    mServer.close();
                }
            } while (!action.equals(ACTION_QUIT));
        } catch (IOException ioe) {
            System.out.println("Couldn't connect to server on port " + SERVER_PORT + "!");
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Api thread failed to determine required action!");
            System.exit(-1);
        }
    }

    private User onLogin(List<String> args) throws IOException {

        // Get arguments
        String username = args.get(0);
        String password = args.get(1);

        Gson gson = new Gson();
        // Serialize credentials
        Credentials credentials = new Credentials(username, password);
        String credentialsJson = gson.toJson(credentials);

        // Serialize request
        Request req = new Request(ACTION_LOGIN, credentialsJson);
        String reqJson = gson.toJson(req);

        // Send request
        mSender.println(reqJson);

        // Get Server response
        String res = mReceiver.readLine();
        Response serverResponse = gson.fromJson(res, Response.class);

        if (serverResponse.getStatus().equals(SUCCESS)) {
            User user = gson.fromJson(serverResponse.getArgument(), User.class);
            return user;
        }

        return null;
    }

    private boolean onSignup(List<String> args) throws IOException {

        // Get arguments
        String username = args.get(0);
        String password = args.get(1);
        String userType = args.get(2);

        Gson gson = new Gson();
        // Serialize user
        User newUser = new User(username, password, userType);
        String newUserJson = gson.toJson(newUser);

        // Serialize request
        Request req = new Request(ACTION_SIGNUP, newUserJson);
        String reqJson = gson.toJson(req);

        // Send request
        mSender.println(reqJson);

        // Get Server response
        String res = mReceiver.readLine();
        Response serverResponse = gson.fromJson(res, Response.class);

        return serverResponse.getStatus().equals(SUCCESS);
    }

    /**
     * Tells the server to find an Uber driver.
     * @param args
     * @return driver The driver assigned to the trip.
     */
    private Ride onUberCall(List<String> args) throws IOException {
        String clientUsername = args.get(0);
        String start = args.get(1);
        String destination = args.get(2);

        Gson gson = new Gson();
        // Serialize user
        Ride newRide = new Ride(start, destination);
        newRide.setClientUsername(clientUsername);
        String newRideJson = gson.toJson(newRide);

        // Serialize request
        Request req = new Request(ACTION_CALL_UBER, newRideJson);
        String reqJson = gson.toJson(req);

        // Send request
        mSender.println(reqJson);

        // Get Server response
        String res = mReceiver.readLine();
        Response serverResponse = gson.fromJson(res, Response.class);

        if (serverResponse.getStatus().equals(SUCCESS)) {
            Ride ride =  gson.fromJson(serverResponse.getArgument(), Ride.class);
            return ride;
        }

        return null;
    }

    private boolean onAcceptRide(List<String> args) throws IOException {
        String updatedRideJson = args.get(0);
        Gson gson = new Gson();

        // Serialize request
        Request req = new Request(ACTION_ACCEPT_RIDE, updatedRideJson);
        String reqJson = gson.toJson(req);

        // Send request
        mSender.println(reqJson);

        // Get Server response
        String res = mReceiver.readLine();
        Response serverResponse = gson.fromJson(res, Response.class);

        if (serverResponse.getStatus().equals(SUCCESS)) {

            return true;
        }

        return false;
    }

    private void onSignalEndTrip(List<String> args) throws IOException {
        Gson gson = new Gson();
        String rideJson = args.get(0);

        // Serialize request
        Request req = new Request(ACTION_SIGNAL_SERVER_END_TRIP, rideJson);
        String reqJson = gson.toJson(req);

        // Send request
        mSender.println(reqJson);

        // Get Server response
        mReceiver.readLine();
    }

    private void onRateUber(List<String> args) throws IOException {
        Gson gson = new Gson();
        String rideJson = args.get(0);

        // Serialize request
        Request req = new Request(ACTION_RATE_UBER, rideJson);
        String reqJson = gson.toJson(req);

        // Send request
        mSender.println(reqJson);

        // Get Server response
        mReceiver.readLine();
    }
}