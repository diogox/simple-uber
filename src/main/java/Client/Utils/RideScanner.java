package Client.Utils;

import Client.SimpleUber;
import Client.Views.Driver.RideAcceptanceScreen;
import Shared.Models.Ride;
import Shared.Models.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.atomic.AtomicBoolean;

public class RideScanner implements Runnable {
    private Thread worker;
    private final AtomicBoolean mRunning = new AtomicBoolean(false);
    private final User mUser;
    private final ThreadChannel mChannel;
    private MulticastSocket mSocket;
    private InetAddress mAddress;
    private Gson mGson;
    private AvailableRideList mAvailableRides;

    public RideScanner(User user, ThreadChannel channel) {
        this.mUser = user;
        this.mChannel = channel;
        this.mGson = new Gson();
        this.mAvailableRides = AvailableRideList.getInstance();

        try {
            mSocket = new MulticastSocket(4446);
            mAddress = InetAddress.getByName("230.0.0.1");
            mSocket.joinGroup(mAddress);
        } catch (IOException e) {
            System.out.println("Failed to secure multicast port on the driver!");
            System.exit(-1);
        }
    }

    public void start() {
        worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        mRunning.set(false);
        AvailableRideList availableRides = AvailableRideList.getInstance();
        availableRides.clear();
    }

    public boolean isRunning() {
        return mRunning.get();
    }

    public void run() {
        mRunning.set(true);

        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        AvailableRideList availableRides = AvailableRideList.getInstance();

        while (mRunning.get()) {
            listen(packet);
            /*
            try {
                Ride ride = new Ride("X", "S");
                Ride ride2 = new Ride("X", "S");
                availableRides.addRide(ride);
                availableRides.addRide(ride2);
                Ride newRide = availableRides.getNext();
                RideAcceptanceScreen.show(mChannel, mUser, newRide);
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            */
        }
        System.out.println("Thread Stopped!");
    }

    private void listen(DatagramPacket packet) {
        try {

            // Receive Message
            mSocket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println(received);
            // Deserialize to Ride
            Ride ride = mGson.fromJson(received, Ride.class);

            // Add to Ride list
            mAvailableRides.addRide(ride);

            // Show ride on screen
            RideAcceptanceScreen.show(mChannel, mUser);
        } catch (IOException e) {
            System.out.println("Failed to receive message as a Driver through Multicast!");
        }
    }
}
