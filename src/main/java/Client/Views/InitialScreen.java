package Client.Views;

import Client.SimpleUber;
import Client.Utils.ThreadChannel;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

public class InitialScreen {

    public static void show(final ThreadChannel channel) {
        Screen screen = SimpleUber.getInstance().mScreen;
        Window window = SimpleUber.getInstance().mWindow;
        screen.clear();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));

        TerminalSize size = new TerminalSize(14, 2);
        ActionListBox actionListBox = new ActionListBox(size);
        actionListBox.addItem("Login", new Runnable() {
            public void run() {
                Login.show(channel);
            }
        });
        actionListBox.addItem("Signup", new Runnable() {
            public void run() {
                Signup.show(channel);
            }
        });

        panel.addComponent(actionListBox);

        // Add panel to window
        window.setComponent(panel);
    }
}
