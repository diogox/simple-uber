package Client.Views.Client;

import Client.Api.ApiActions;
import Client.SimpleUber;
import Client.Utils.ThreadChannel;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

public class UberInTransitScreen {

    public static void show(final ThreadChannel channel,
                            final String driverUsername) {
        Screen screen = SimpleUber.getInstance().mScreen;
        Window window = SimpleUber.getInstance().mWindow;
        System.out.println(1);
        screen.clear();
        System.out.println(2);

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));

        new Label("You are now travelling with " + driverUsername + "!").addTo(panel);

        // Add panel to window
        window.setComponent(panel);

        new Thread(new Runnable() {
            public void run() {
                // Wait for the driver to signal the end of the ride
                ApiActions.waitForEndTrip(channel);
                System.out.println("Ride ended. Moving to Rating screen now!");

                // Show rating screen
                StarRatingScreen.show(channel, driverUsername);
            }
        }).start();
    }
}
