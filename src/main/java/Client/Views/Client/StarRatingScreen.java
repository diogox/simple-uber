package Client.Views.Client;

import Client.Api.ApiActions;
import Client.SimpleUber;
import Client.Utils.ThreadChannel;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

public class StarRatingScreen {

    public static void show(final ThreadChannel channel, final String driverUsername) {
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
                ApiActions.rateDriver(channel, driverUsername, "1");
            }
        });
        actionListBox.addItem("2 Star", new Runnable() {
            public void run() {
                ApiActions.rateDriver(channel, driverUsername, "2");
            }
        });
        actionListBox.addItem("3 Star", new Runnable() {
            public void run() {
                ApiActions.rateDriver(channel, driverUsername, "3");
            }
        });
        actionListBox.addItem("4 Star", new Runnable() {
            public void run() {
                ApiActions.rateDriver(channel, driverUsername, "4");
            }
        });
        actionListBox.addItem("5 Star", new Runnable() {
            public void run() {
                ApiActions.rateDriver(channel, driverUsername, "5");
            }
        });


        // Add panel to window
        window.setComponent(panel);
    }
}
