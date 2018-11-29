package Client.Views;

import Client.Api.ApiActions;
import Client.SimpleUber;
import Client.Utils.ThreadChannel;
import Client.Views.Client.ClientMenu;
import Client.Views.Driver.DriverMenu;
import Shared.Models.User;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.screen.Screen;

public class Login {

    public static void show(final ThreadChannel channel) {
        Screen screen = SimpleUber.getInstance().mScreen;
        Window window = SimpleUber.getInstance().mWindow;

        screen.clear();
        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        final TextBox usernameInput = new TextBox();
        panel.addComponent(new Label("Username"));
        panel.addComponent(usernameInput);

        final TextBox passwordInput = new TextBox();
        panel.addComponent(new Label("Password"));
        panel.addComponent(passwordInput);

        panel.addComponent(new EmptySpace(new TerminalSize(1,1)));
        panel.addComponent(new EmptySpace(new TerminalSize(1,1)));

        Button loginButton = new Button("Login");
        panel.addComponent(loginButton);
        Button backButton = new Button("Main Menu");
        panel.addComponent(backButton);

        loginButton.addListener(new Button.Listener() {
            public void onTriggered(Button button) {
                String username = usernameInput.getText();
                String password = passwordInput.getText();
                User user = ApiActions.login(channel, username, password);

                if (user != null) {
                    if (user.getUserType().equals("Client")) {
                        ClientMenu.show(channel, user);

                    } else if (user.getUserType().equals("Driver")) {
                        DriverMenu.show(channel, user);

                    }
                } else {
                    new MessageDialogBuilder()
                            .setTitle("Error")
                            .setText("Incorrect Username or Password")
                            .build()
                            .showDialog(SimpleUber.getInstance().mDialogWindow);
                    Login.show(channel);
                }
            }
        });
        backButton.addListener(new Button.Listener() {
            public void onTriggered(Button button) {
                InitialScreen.show(channel);
            }
        });

        // Add panel to window
        window.setComponent(panel);
    }
}
