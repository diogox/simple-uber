package Client.Api;

import Client.Utils.ThreadChannel;
import Shared.Models.Ride;
import Shared.Models.User;
import com.google.gson.Gson;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import static Shared.Models.Actions.*;
import static Shared.Models.Status.FAILURE;
import static Shared.Models.Status.SUCCESS;

public class ApiActions {

    public static User login(ThreadChannel channel,
                             String username,
                             String password)
    {
        List<String> loginArgs = new ArrayList<String>();
        loginArgs.add(username);
        loginArgs.add(password);

        channel.sendToOtherThread(ACTION_LOGIN, loginArgs);
        ThreadChannel.Data data = channel.receiveOnMainThread();
        String responseAction = data.getAction();

        System.out.println(responseAction);

        if (responseAction.equals(SUCCESS)) {
            String userType = data.getArgs().get(0);
            return new User(username, password, userType);
        } else if (responseAction.equals(FAILURE)) {
            return null;
        } else {
            System.out.println("Error in login. Wrong message from server!");
            System.exit(-1);
            return null;
        }
    }

    public static boolean signup(ThreadChannel channel,
                                String username,
                                String password,
                                String userType)
    {
        List<String> signupArgs = new ArrayList<String>();
        signupArgs.add(username);
        signupArgs.add(password);
        signupArgs.add(userType);

        channel.sendToOtherThread(ACTION_SIGNUP, signupArgs);
        ThreadChannel.Data data = channel.receiveOnMainThread();
        String responseAction = data.getAction();
        System.out.println(responseAction);

        if (responseAction.equals(SUCCESS)) {
            return true;
        } else if (responseAction.equals(FAILURE)) {
            return false;
        } else {
            System.out.println("Error in signup. Wrong message from server!");
            System.exit(-1);
            return false;
        }
    }

    public static String callUber(ThreadChannel channel,
                                   String clientUsername,
                                   String start,
                                   String destination) {
        List<String> callUberArgs = new ArrayList<String>();
        callUberArgs.add(clientUsername);
        callUberArgs.add(start);
        callUberArgs.add(destination);

        channel.sendToOtherThread(ACTION_CALL_UBER, callUberArgs);
        ThreadChannel.Data data = channel.receiveOnMainThread();
        String responseAction = data.getAction();
        System.out.println(responseAction);

        if (responseAction.equals(SUCCESS)) {
            String driverUsername = data.getArgs().get(0);
            return driverUsername;
        } else if (responseAction.equals(FAILURE)) {
            return null;
        } else {
            System.out.println("Error in call uber. Wrong message from server!");
            System.exit(-1);
            return null;
        }
    }

    public static boolean acceptRide(ThreadChannel channel, Ride ride) {
        List<String> acceptUberArgs = new ArrayList<String>();
        Gson gson = new Gson();
        String rideJson = gson.toJson(ride);
        acceptUberArgs.add(rideJson);

        channel.sendToOtherThread(ACTION_ACCEPT_RIDE, acceptUberArgs);
        ThreadChannel.Data data = channel.receiveOnMainThread();
        String responseAction = data.getAction();
        System.out.println(responseAction);

        if (responseAction.equals(SUCCESS)) {
            System.out.println("Ride Accepted!");
            return true;
        } else if (responseAction.equals(FAILURE)) {
            System.out.println("Ride was already taken!");
            return false;
        } else {
            System.out.println("Error in accept uber. Wrong message from server!");
            System.exit(-1);
            return false;
        }
    }

    public static void waitForEndTrip(ThreadChannel channel) {

        // Tell the other thread to wait for the server to signal the end of the trip
        List<String> args = new ArrayList<String>();
        channel.sendToOtherThread(ACTION_SIGNAL_CLIENT_END_TRIP, args);

        // Wait until the driver signals the end of the ride
        channel.receiveOnMainThread();
    }

    public static void signalEndOfTrip(ThreadChannel channel, String rideJson) {
        List<String> args = new ArrayList<String>();
        args.add(rideJson);

        channel.sendToOtherThread(ACTION_SIGNAL_SERVER_END_TRIP, args);
        ThreadChannel.Data data = channel.receiveOnMainThread();
        String responseAction = data.getAction();
        System.out.println(responseAction);
    }

    public static boolean rateDriver(ThreadChannel channel, String driverUsername, String rating) {

        // create mock Ride object
        Ride incompleteRide = new Ride("", "");
        incompleteRide.setDriverUsername(driverUsername);
        incompleteRide.setRating(rating);

        // Serialize it
        Gson gson = new Gson();
        String rideJson = gson.toJson(incompleteRide);

        List<String> ratingArgs = new ArrayList<String>();
        ratingArgs.add(rideJson);

        channel.sendToOtherThread(ACTION_RATE_UBER, ratingArgs);
        ThreadChannel.Data data = channel.receiveOnMainThread();
        String responseAction = data.getAction();
        System.out.println(responseAction);

        if (responseAction.equals(SUCCESS)) {
            return true;
        } else if (responseAction.equals(FAILURE)) {
            return false;
        } else {
            System.out.println("Error in rating an uber driver. Wrong message from server!");
            System.exit(-1);
            return false;
        }
    }
}
