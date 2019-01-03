package Client.Views.Driver;

import Client.SimpleUber;
import Client.Utils.ThreadChannel;
import Client.Views.Client.ClientMenu;
import Shared.Models.Ride;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;

import java.util.List;

public class DriverHistory {
    private static int mStartIndex = 0;
    private static int mStopIndex = 10;

    public static void show(final ThreadChannel channel, final List<Ride> rides) {
        Screen screen = SimpleUber.getInstance().mScreen;
        Window window = SimpleUber.getInstance().mWindow;
        screen.clear();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        Table<String> table = new Table<String>("Client", "Start Location", "End Location", "Rating");
        table.getTableModel().addRow("------", "--------------", "------------", "------");

        boolean isEndOfList = false;
        for (int i = mStartIndex; i < mStopIndex; i++) {

            try {
                Ride ride = rides.get(i);
                table.getTableModel().addRow(ride.getClientUsername(),
                        ride.getStartLocation(),
                        ride.getEndLocation(),
                        ride.getRating() + "/5");
            } catch (IndexOutOfBoundsException iobe) {
                isEndOfList = true;
                break;
            }
        }

        new Button("Main Menu", new Runnable() {
            public void run() {
                DriverMenu.show(channel, DriverMenu.mUser);
            }
        }).addTo(panel);

        if (!isEndOfList) {
            new Button("Next Page", new Runnable() {
                public void run() {
                    mStartIndex += 10;
                    mStopIndex += 10;
                    DriverHistory.show(channel, rides);
                }
            }).addTo(panel);
        } else {
            panel.addComponent(new EmptySpace(new TerminalSize(1,1)));
        }

        panel.addComponent(table);

        // Add panel to window
        window.setComponent(panel);
    }
}
