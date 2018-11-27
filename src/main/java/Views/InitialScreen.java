package Views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

import java.util.Arrays;

public class InitialScreen {

    public static void show(final Screen screen, final Window window) {
        screen.clear();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));

        TerminalSize size = new TerminalSize(14, 2);
        ActionListBox actionListBox = new ActionListBox(size);
        actionListBox.addItem("Login", new Runnable() {
            public void run() {
                Login.show(screen, window);
            }
        });
        actionListBox.addItem("Signup", new Runnable() {
            public void run() {
                Signup.show(screen, window);
            }
        });

        panel.addComponent(actionListBox);

        // Add panel to window
        window.setComponent(panel);
    }
}
