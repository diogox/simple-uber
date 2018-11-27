package Client.Views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;

public class DriverMenu {
    public static boolean isAvailable = false;

    public static void show(final Screen screen, final Window window) {
        screen.clear();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));

        TerminalSize size = new TerminalSize(20, 3);
        ActionListBox actionListBox = new ActionListBox(size);
        actionListBox.addItem("Become 'Available'", new Runnable() {
            public void run() {
                isAvailable = true;
                System.out.println("Became available to take rides!");
            }
        });
        actionListBox.addItem("Become 'Unavailable'", new Runnable() {
            public void run() {
                isAvailable = false;
                System.out.println("Became unavailable to take rides!");
            }
        });
        actionListBox.addItem("Sign Out", new Runnable() {
            public void run() {
                InitialScreen.show(screen, window);
            }
        });

        panel.addComponent(actionListBox);

        // Add panel to window
        window.setComponent(panel);
    }
}
