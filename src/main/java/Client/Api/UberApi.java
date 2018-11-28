package Client.Api;

import Client.Utils.ThreadChannel;
import Shared.Models.Credentials;
import Shared.Models.Request;
import Shared.Models.Response;
import Shared.Models.User;
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
                        loginArgs.add(user.getUserType())
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
                    // TODO: Make login request to the server

                    List<String> callUberArgs = new ArrayList<String>();
                    mChannel.sendToMainThread(SUCCESS, callUberArgs);
                } else if (action.equals(ACTION_QUIT)) {
                    System.out.println("Quitting!");
                    mServer.close();
                }
            } while (!action.equals(ACTION_QUIT));
        } catch (IOException ioe) {
            System.out.println("Couldn't connect to server on port " + SERVER_PORT + "!");
            System.exit(-1);
        } catch (Exception e) {
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
            User user =  gson.fromJson(serverResponse.getArgument(), User.class);
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
        Request req = new Request(ACTION_LOGIN, newUserJson);
        String reqJson = gson.toJson(req);

        // Send request
        mSender.println(reqJson);

        // Get Server response
        String res = mReceiver.readLine();
        Response serverResponse = gson.fromJson(res, Response.class);

        return serverResponse.getStatus().equals(SUCCESS);
    }
}