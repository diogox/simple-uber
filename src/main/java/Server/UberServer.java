package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UberServer {
    private static final int SERVER_PORT = 3322;
    public static Auth mAuth;
    private ServerSocket mServerSocket = null;

    public UberServer() {
        mAuth = new Auth();
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

        // Inciar o processamento dos pedidos
        while(true) {

            try {
                // Receber conexão
                clientSocket = mServerSocket.accept();

                // Launch thread for handling the conection
                ConnectionHandler handler = new ConnectionHandler(clientSocket);
                new Thread(handler).start();

            } catch (IOException e) {
                System.out.println("Error receiving client connection!");
            }
        }

        /*
        // Close serverSocket
        try {
            mServerSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing server socket!");
            System.exit(-1);
        }
        */
    }

    public static void main(String[] args) {
        new UberServer().start();
    }
}
