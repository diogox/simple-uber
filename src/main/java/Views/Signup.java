package Views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

import java.util.Arrays;

public class Signup {

    public static void show(final Screen screen, final Window window) {
        screen.clear();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        panel.addComponent(new Label("Username"));
        panel.addComponent(new TextBox());

        panel.addComponent(new Label("Password"));
        panel.addComponent(new TextBox());

        panel.addComponent(new EmptySpace(new TerminalSize(0,0))); // Empty space underneath labels
        Button signup = new Button("Signup");
        signup.addListener(new Button.Listener() {
            public void onTriggered(Button button) {
                Login.show(screen, window);
            }
        });
        panel.addComponent(signup);

        // Add panel to window
        window.setComponent(panel);
    }
}
