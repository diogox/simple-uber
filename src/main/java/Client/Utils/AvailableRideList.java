package Client.Utils;

import Shared.Models.Ride;

import java.util.*;

public class AvailableRideList {
    private static AvailableRideList mInstance = null;
    private LinkedList<Ride> mRides;

    private AvailableRideList() {
        mRides = new LinkedList<Ride>();
    }

    public static AvailableRideList getInstance() {

        if (mInstance == null) {
            mInstance = new AvailableRideList();
        }

        return mInstance;
    }

    public void addRide(Ride ride) {
        mRides.addLast(ride);
    }

    public Ride seeNext() {
        try {
            Ride next = mRides.getFirst();

            return next;
        } catch (NoSuchElementException nsee) {
            return null;
        }

    }

    public void removeNext() {
        mRides.removeFirst();
    }

    public boolean hasNext() {
        return !mRides.isEmpty();
    }

    public void clear() {
        mRides.clear();
    }
}
