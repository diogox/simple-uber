package Server;

import Shared.Models.Ride;
import Shared.Models.User;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Auth {

    // Path to file with list
    private final String pathToFile = "users.txt";
    // List
    private List<User> users;


    public Auth() {
        this.users = loadUsers();
    }


    public synchronized User login(String username, String password) {

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }

        return null;
    }

    public synchronized boolean signup(String username, String password, String typeUser) {

        if (!users.isEmpty())
            for (User u : users)
                if (u.getUsername().equals(username)) // If username already exists
                    return false;

        try {

            FileWriter fileWriter = new FileWriter(pathToFile);

            User newUser = new User(username, password, typeUser);

            users.add(newUser);

            String regists = new Gson().toJson(users, ArrayList.class);

            fileWriter.write(regists);

            fileWriter.flush();
            fileWriter.close();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void saveUserRide(String username, Ride ride) {

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.addRide(ride);
            }
        }

        try {

            FileWriter fileWriter = new FileWriter(pathToFile);

            String usersJson = new Gson().toJson(users, ArrayList.class);

            fileWriter.write(usersJson);
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<Ride> getUserHistory(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user.getHistory();
            }
        }

        return null;
    }

    private ArrayList<User> loadUsers() {

        ArrayList<User> list = null;

        try {

            // Read file
            BufferedReader bufferedReader = new BufferedReader(new FileReader(pathToFile));
            // If file contains a list, get this list
            list = new Gson().fromJson(bufferedReader.readLine(), ArrayList.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (list == null) // If file haven't data

            return new ArrayList<User>();

        else {
            ArrayList<User> newList = new ArrayList<User>();

            // Cast each user in list and add to new list
            for (int i = 0; i < list.size(); i++) {
                User newUser = new Gson().fromJson(String.valueOf(list.get(i)), User.class);
                newList.add(newUser);
            }

            return newList;
        }

    }

}
