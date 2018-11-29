package Client.Views.Driver;

import Client.Api.ApiActions;
import Client.SimpleUber;
import Client.Utils.AvailableRideList;
import Client.Utils.ThreadChannel;
import Shared.Models.Ride;
import Shared.Models.User;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.screen.Screen;

public class RideAcceptanceScreen {

    public static void show(final ThreadChannel channel, final User user) {
        Screen screen = SimpleUber.getInstance().mScreen;
        Window window = SimpleUber.getInstance().mWindow;
        screen.clear();

        // get ride
        final AvailableRideList availableRides = AvailableRideList.getInstance();
        final Ride ride = availableRides.seeNext();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        // Show the name of the client
        new Label("A ride has been requested by " + ride.getClientUsername()).addTo(panel);
        panel.addComponent(new EmptySpace(new TerminalSize(1,1)));


        // Show the name of the client
        new Label("From: ").addTo(panel);
        new Label(ride.getStartLocation()).addTo(panel);
        // Show the name of the client
        new Label("To: ").addTo(panel);
        new Label(ride.getEndLocation()).addTo(panel);

        new Button("Accept", new Runnable() {
            public void run() {

                // Removes the one we're currently seeing
                availableRides.clear();

                // Add driver's name
                ride.setDriverUsername(user.getUsername());

                // Send request
                boolean wasAccepted = ApiActions.acceptRide(channel, ride);

                if (wasAccepted) {
                    DuringRideScreen.show(channel, user, ride);
                } else {
                    new MessageDialogBuilder()
                            .setTitle("Error")
                            .setText("Ride has already been taken by another driver!")
                            .build()
                            .showDialog(SimpleUber.getInstance().mDialogWindow);

                    DriverMenu.show(channel, user);
                }
            }
        }).addTo(panel);

        new Button("Decline", new Runnable() {
            public void run() {
                // Removes the one we're currently seeing
                availableRides.removeNext();

                if (availableRides.hasNext()) {
                    RideAcceptanceScreen.show(channel, user);
                } else {
                    DriverMenu.show(channel, user);
                }
            }
        }).addTo(panel);

        // Add panel to window
        window.setComponent(panel);
    }
}
