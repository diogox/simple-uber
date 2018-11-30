package Server;

import Shared.Models.Ride;

import java.util.ArrayList;
import java.util.List;

public class RideSystem {
    private static RideSystem mInstance = null;
    private List<Ride> mActiveRides;

    private RideSystem() {
        mActiveRides = new ArrayList<Ride>();
    }

    public static RideSystem getInstance() {
        if (mInstance == null) {
            mInstance = new RideSystem();
        }

        return mInstance;
    }

    public synchronized void addRide(Ride ride) {
        mActiveRides.add(ride);
    }

    public synchronized void removeRide(Ride ride) {
        mActiveRides.remove(ride);
    }

    public synchronized Ride getRideByClient(String clientUsername, long rideTimestamp) {

        for (Ride ride : mActiveRides) {
            if (ride.getClientUsername().equals(clientUsername) && ride.getTimestamp() == rideTimestamp) {
                return ride;
            }
        }
        return null;
    }

    public synchronized Ride getRideByDriver(String driverUsername) {

        for (Ride ride : mActiveRides) {
            if (ride.getDriverUsername().equals(driverUsername)) {
                return ride;
            }
        }
        return null;
    }
}
