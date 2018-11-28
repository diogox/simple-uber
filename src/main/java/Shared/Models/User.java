package Shared.Models;

import java.util.ArrayList;

public class User {
    private Credentials credentials;
    private String userType;
    private ArrayList<Ride> rideHistory;

    public User(String username, String password, String userType) {
        this.credentials = new Credentials(username, password);
        this.userType = userType;
        this.rideHistory = new ArrayList<Ride>();
    }

    public String getUsername() {
        return credentials.getUsername();
    }

    public String getPassword() {
        return credentials.getPassword();
    }

    public String getUserType() {
        return userType;
    }

    public ArrayList<Ride> getHistory() {
        return rideHistory;
    }

    public void addRide(Ride ride) {
        rideHistory.add(ride);
    }
}
