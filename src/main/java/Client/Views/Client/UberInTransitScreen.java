package Client.Views.Client;

import Client.SimpleUber;
import Client.Utils.ThreadChannel;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

public class UberInTransitScreen {

    public static void show(final ThreadChannel channel,
                            String driverUsername) {
        Screen screen = SimpleUber.getInstance().mScreen;
        Window window = SimpleUber.getInstance().mWindow;
        screen.clear();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));

        new Label("You are now travelling with " + driverUsername + "!");

        // Add panel to window
        window.setComponent(panel);
    }
}
