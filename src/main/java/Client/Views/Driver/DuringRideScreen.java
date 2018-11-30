package Client.Views.Driver;

import Client.Api.ApiActions;
import Client.SimpleUber;
import Client.Utils.ThreadChannel;
import Shared.Models.Ride;
import Shared.Models.User;
import com.google.gson.Gson;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

public class DuringRideScreen {

    public static void show(final ThreadChannel channel,
                            final User user,
                            final Ride ride) {
        Screen screen = SimpleUber.getInstance().mScreen;
        Window window = SimpleUber.getInstance().mWindow;
        screen.clear();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));

        new Label("Travelling with client:").addTo(panel);
        new Label(ride.getClientUsername()).addTo(panel);

        panel.addComponent(new EmptySpace(new TerminalSize(1,1)));

        new Button("End of Ride", new Runnable() {
            public void run() {
                Gson gson = new Gson();
                String rideJson = gson.toJson(ride);

                ApiActions.signalEndOfTrip(channel, rideJson);
                ride.setRating("Pending..");
                DriverMenu.mUser.addRide(ride);
                DriverMenu.show(channel, user);
            }
        }).addTo(panel);

        // Add panel to window
        window.setComponent(panel);
    }
}
