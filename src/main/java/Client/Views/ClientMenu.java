package Client.Views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;

public class ClientMenu {

    public static void show(final Screen screen, final Window window) {
        screen.clear();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));

        TerminalSize size = new TerminalSize(14, 2);
        ActionListBox actionListBox = new ActionListBox(size);
        actionListBox.addItem("Call Uber", new Runnable() {
            public void run() {
                System.out.println("Calling Uber!");
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
