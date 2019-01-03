package Client.Views.Driver;

import Client.SimpleUber;
import Client.Utils.RideScanner;
import Client.Utils.ThreadChannel;
import Client.Views.Client.ClientHistory;
import Client.Views.InitialScreen;
import Shared.Models.Ride;
import Shared.Models.User;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

import java.util.List;

public class DriverMenu {
    public static boolean isAvailable = true;
    public static RideScanner mRideScanner;
    public static User mUser;

    public static void show(final ThreadChannel channel, final User user) {
        mUser = user;
        final Screen screen = SimpleUber.getInstance().mScreen;
        final Window window = SimpleUber.getInstance().mWindow;
        screen.clear();

        if (mRideScanner == null || !mRideScanner.isRunning() && isAvailable) {
            mRideScanner = new RideScanner(user, channel);
            mRideScanner.start();
            System.out.println("Started Ride Scanner!");
        }

        // Create panel to hold components
        final Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));


        TerminalSize size = new TerminalSize(30, 3);
        ActionListBox actionListBox = new ActionListBox(size);
        if (isAvailable) {
            new Label("Currently: 'Available'").addTo(panel);
            new EmptySpace(new TerminalSize(1, 1)).addTo(panel);
            actionListBox.addItem("Become 'Unavailable'", new Runnable() {
                public void run() {
                    System.out.println("Stopped RideScanner");
                    // Stop multicast scanner
                    mRideScanner.pause();

                    // Render 'unavailable' view
                    isAvailable = false;
                    DriverMenu.show(channel, user);
                }
            });
        } else {
            new Label("Currently: 'Unavailable'").addTo(panel);
            new EmptySpace(new TerminalSize(1, 1)).addTo(panel);
            actionListBox.addItem("Become 'Available'", new Runnable() {
                public void run() {
                    System.out.println("Started RideScanner");
                    // Start multicast scanner
                    mRideScanner.resume();

                    // Render 'available' view
                    isAvailable = true;
                    DriverMenu.show(channel, user);
                }
            });
        }
        actionListBox.addItem("See History", new Runnable() {
            public void run() {
                List<Ride> rides = user.getHistory();
                DriverHistory.show(channel, rides);
            }
        });
        actionListBox.addItem("Sign Out", new Runnable() {
            public void run() {
                mRideScanner.stop();
                mRideScanner = null;
                InitialScreen.show(channel);
            }
        });

        panel.addComponent(actionListBox);

        // Add panel to window
        window.setComponent(panel);
    }
}
