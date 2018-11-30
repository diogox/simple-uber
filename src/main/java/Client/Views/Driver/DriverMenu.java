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
    public static boolean isAvailable = false;
    public static RideScanner mRideScanner;
    public static User mUser;

    public static void show(final ThreadChannel channel, final User user) {
        mUser = user;
        final Screen screen = SimpleUber.getInstance().mScreen;
        final Window window = SimpleUber.getInstance().mWindow;
        screen.clear();

        if (mRideScanner == null || !mRideScanner.isRunning()) {
            mRideScanner = new RideScanner(user, channel);
            mRideScanner.start();
        }

        // Create panel to hold components
        final Panel panel = new Panel();

        renderView(screen, panel, window, channel, user, mRideScanner);
    }

    private static void renderView(final Screen screen,
                                   final Panel panel,
                                   final Window window,
                                   final ThreadChannel channel,
                                   final User user,
                                   final RideScanner rideScannerRunnable) {
        screen.clear();
        panel.setLayoutManager(new GridLayout(1));


        TerminalSize size = new TerminalSize(30, 3);
        ActionListBox actionListBox = new ActionListBox(size);
        if (isAvailable) {
            new Label("Currently: 'Available'").addTo(panel);
            new EmptySpace(new TerminalSize(1, 1)).addTo(panel);
            actionListBox.addItem("Become 'Unavailable'", new Runnable() {
                public void run() {
                    isAvailable = false;
                    screen.clear();
                    DriverMenu.renderView(screen, panel, window, channel, user, rideScannerRunnable);
                }
            });
        } else {
            new Label("Currently: 'Unavailable'").addTo(panel);
            new EmptySpace(new TerminalSize(1, 1)).addTo(panel);
            actionListBox.addItem("Become 'Available'", new Runnable() {
                public void run() {
                    isAvailable = true;
                    screen.clear();
                    DriverMenu.renderView(screen, panel, window, channel, user, rideScannerRunnable);
                }
            });
        }
        actionListBox.addItem("See History", new Runnable() {
            public void run() {
                List<Ride> rides = user.getHistory();
                ClientHistory.show(channel, rides);
            }
        });
        actionListBox.addItem("Sign Out", new Runnable() {
            public void run() {
                rideScannerRunnable.stop();
                mRideScanner = null;
                InitialScreen.show(channel);
                return;
            }
        });

        panel.addComponent(actionListBox);

        // Add panel to window
        window.setComponent(panel);
    }
}
