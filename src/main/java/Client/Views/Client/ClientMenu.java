package Client.Views.Client;

import Client.Api.ApiActions;
import Client.SimpleUber;
import Client.Utils.ThreadChannel;
import Client.Views.InitialScreen;
import Shared.Models.Ride;
import Shared.Models.User;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;

import java.util.List;

public class ClientMenu {
    public static User mUser;

    public static void show(final ThreadChannel channel, final User user) {
        mUser = user;
        Screen screen = SimpleUber.getInstance().mScreen;
        Window window = SimpleUber.getInstance().mWindow;
        screen.clear();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));

        TerminalSize size = new TerminalSize(14, 3);
        ActionListBox actionListBox = new ActionListBox(size);
        actionListBox.addItem("Call Uber", new Runnable() {
            public void run() {
                CallUberScreen.show(channel, user);
            }
        });
        actionListBox.addItem("See History", new Runnable() {
            public void run() {
                List<Ride> rides = user.getHistory();
                ClientHistory.show(channel, rides);
            }
        });
        actionListBox.addItem("Sign Out", new Runnable() {
            public void run() {
                InitialScreen.show(channel);
            }
        });

        panel.addComponent(actionListBox);

        // Add panel to window
        window.setComponent(panel);
    }
}
