package Client.Views.Client;

import Client.Api.ApiActions;
import Client.SimpleUber;
import Client.Utils.ThreadChannel;
import Shared.Models.Ride;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

public class StarRatingScreen {

    public static void show(final ThreadChannel channel, final Ride ride) {
        Screen screen = SimpleUber.getInstance().mScreen;
        Window window = SimpleUber.getInstance().mWindow;
        screen.clear();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(5));

        TerminalSize size = new TerminalSize(14, 5);
        final ActionListBox actionListBox = new ActionListBox(size);

        actionListBox.addItem("1 Star", new Runnable() {
            public void run() {
                ApiActions.rateDriver(channel, ride.getDriverUsername(), "1");
                ride.setRating("1");
                ClientMenu.mUser.addRide(ride);
                ClientMenu.show(channel, ClientMenu.mUser);
            }
        });
        actionListBox.addItem("2 Star", new Runnable() {
            public void run() {
                ApiActions.rateDriver(channel, ride.getDriverUsername(), "2");
                ride.setRating("2");
                ClientMenu.mUser.addRide(ride);
                ClientMenu.show(channel, ClientMenu.mUser);
            }
        });
        actionListBox.addItem("3 Star", new Runnable() {
            public void run() {
                ApiActions.rateDriver(channel, ride.getDriverUsername(), "3");
                ride.setRating("3");
                ClientMenu.mUser.addRide(ride);
                ClientMenu.show(channel, ClientMenu.mUser);
            }
        });
        actionListBox.addItem("4 Star", new Runnable() {
            public void run() {
                ApiActions.rateDriver(channel, ride.getDriverUsername(), "4");
                ride.setRating("4");
                ClientMenu.mUser.addRide(ride);
                ClientMenu.show(channel, ClientMenu.mUser);
            }
        });
        actionListBox.addItem("5 Star", new Runnable() {
            public void run() {
                ApiActions.rateDriver(channel, ride.getDriverUsername(), "5");
                ride.setRating("5");
                ClientMenu.mUser.addRide(ride);
                ClientMenu.show(channel, ClientMenu.mUser);
            }
        });
        panel.addComponent(actionListBox);

        // Add panel to window
        window.setComponent(panel);
    }
}
