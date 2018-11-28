package Client.Api;

import Client.Utils.ThreadChannel;
import java.util.ArrayList;
import java.util.List;

import static Shared.Models.Actions.*;
import static Shared.Models.Status.FAILURE;
import static Shared.Models.Status.SUCCESS;

public class ApiActions {

    // TODO: Change boolean return value to user (so we can know usertype)
    public static boolean login(ThreadChannel channel,
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
            return true;
        } else if (responseAction.equals(FAILURE)) {
            return false;
        } else {
            System.out.println("Error in login. Wrong message from server!");
            System.exit(-1);
            return false;
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

    public static boolean callUber(ThreadChannel channel,
                                   String start,
                                   String destination) {
        List<String> callUberArgs = new ArrayList<String>();
        callUberArgs.add(start);
        callUberArgs.add(destination);

        channel.sendToOtherThread(ACTION_CALL_UBER, callUberArgs);
        ThreadChannel.Data data = channel.receiveOnMainThread();
        String responseAction = data.getAction();
        System.out.println(responseAction);

        if (responseAction.equals(SUCCESS)) {
            return true;
        } else if (responseAction.equals(FAILURE)) {
            return false;
        } else {
            System.out.println("Error in call uber. Wrong message from server!");
            System.exit(-1);
            return false;
        }
    }

    public static void waitForEndTrip(ThreadChannel channel) {
        channel.receiveOnMainThread();
        System.out.println("Trip Ended!");
    }

    public static boolean rateDriver(ThreadChannel channel,
                                  String driver,
                                  String rating) {
        List<String> ratingArgs = new ArrayList<String>();
        ratingArgs.add(driver);
        ratingArgs.add(rating);

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
