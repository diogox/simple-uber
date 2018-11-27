package Client.Views;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

public class Login {

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
        Button loginButton = new Button("Login");
        panel.addComponent(loginButton);

        loginButton.addListener(new Button.Listener() {
            public void onTriggered(Button button) {
                ClientMenu.show(screen, window);
            }
        });

        // Add panel to window
        window.setComponent(panel);
    }
}
