package Server;

import Shared.Models.Credentials;
import Shared.Models.Request;
import Shared.Models.Response;
import Shared.Models.User;
import com.google.gson.Gson;
import org.omg.PortableInterceptor.SUCCESSFUL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static Shared.Models.Actions.*;
import static Shared.Models.Status.*;

public class UberServer {
    private static final int SERVER_PORT = 3322;
    private ServerSocket mServerSocket = null;
    private PrintWriter mSender;
    private BufferedReader mReceiver;

    public UberServer() {

        try {
            mServerSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Uber server listening on port " + SERVER_PORT + "!");
        } catch(IOException ioe) {
            System.out.println("Port " + SERVER_PORT + " already in use, server couldn't connect!");
            System.exit(-1);
        }
    }

    public void start() {
        Socket clientSocket = null;

        try {
            // Inciar o processamento dos pedidos
            clientSocket = mServerSocket.accept();

            mSender = new PrintWriter(clientSocket.getOutputStream(), true);
            mReceiver = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));

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

                } else if(reqAction.equals(ACTION_LOGIN)) {

                    // Signup the user
                    Credentials credentials = gson.fromJson(reqArgs, Credentials.class);
                    User user = handleLogin(credentials);

                    // Send answer back to the client
                    Response res;
                    if (user != null) {
                        res = new Response(SUCCESS, user.getUserType());
                    } else {
                        res = new Response(FAILURE);
                    }

                    // Serialize response
                    String resJson = gson.toJson(res);

                    // Send response to client
                    mSender.println(resJson);

                } else if(reqAction.equals(ACTION_CALL_UBER)) {
                    // TODO: Implement multicasting logic
                } else if(reqAction.equals(ACTION_RATE_UBER)) {
                    // TODO: Find driver and client, add a 'Ride' to his history
                }
                System.out.println(clientRequest);
            }
            mServerSocket.close();

        } catch (IOException e) {
            System.out.println("Accept_failed!");
            System.exit(-1);
        }
    }

    private boolean handleSignup(User newUser) {
        String username = newUser.getUsername();
        String password = newUser.getPassword();
        String userType = newUser.getUserType();

        // TODO: Add user to the user list

        return true;
    }

    private User handleLogin(Credentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        // TODO: Verify if user exists

        return new User("dxmp", "1234", "Client");
    }

    public static void main(String[] args) {
        new UberServer().start();
    }
}
