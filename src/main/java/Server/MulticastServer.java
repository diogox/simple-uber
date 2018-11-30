package Server;

import Shared.Models.Ride;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.*;

public class MulticastServer {
    private static final String mGroupName = "230.0.0.1";
    private static DatagramSocket mSocket = null;

    public void notifyDrivers(Ride ride) {
        startServer();

        Gson gson = new Gson();
        String rideJson = gson.toJson(ride);
        sendDriversRequest(rideJson);

        /*
        String rideJson = gson.toJson(ride);

        try {
            byte[] rideJsonBytes = rideJson.getBytes();
            InetAddress group = InetAddress.getByName(mGroupName);
            DatagramPacket packet = new DatagramPacket(rideJsonBytes, rideJsonBytes.length, group, 4446);

            mSocket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    private void startServer() {
        // Port 0 means that the socket will find any port that is available
        int port = 0;
        try {
            mSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("Couldn't secure DatagramSocket on port " + port + "on the server!");
        }
    }

    private void sendDriversRequest(String rideJson) {
        try {
            byte[] message = rideJson.getBytes();
            InetAddress group = InetAddress.getByName(mGroupName);
            DatagramPacket packet = new DatagramPacket(message, message.length, group, 4446);
            mSocket.send(packet);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
