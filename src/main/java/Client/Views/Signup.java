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

public class Signup {

    public static void show(final ThreadChannel channel) {
        Screen screen = SimpleUber.getInstance().mScreen;
        Window window = SimpleUber.getInstance().mWindow;
        screen.clear();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        // Username
        panel.addComponent(new Label("Username"));
        final TextBox usernameInput = new TextBox();
        panel.addComponent(usernameInput);

        // Password
        panel.addComponent(new Label("Password"));
        final TextBox passwordInput = new TextBox();
        panel.addComponent(passwordInput);

        // Type fo User
        panel.addComponent(new Label("User Type"));
        TerminalSize size = new TerminalSize(14, 2);
        final RadioBoxList<String> userTypeComboBox = new RadioBoxList<String>(size);
        userTypeComboBox.addItem("Client");
        userTypeComboBox.addItem("Driver");
        panel.addComponent(userTypeComboBox);

        // Whitespace
        panel.addComponent(new EmptySpace(new TerminalSize(1,1))); // Empty space underneath labels
        panel.addComponent(new EmptySpace(new TerminalSize(1,1))); // Empty space underneath labels

        // Buttons
        Button signupButton = new Button("Signup");
        Button backButton = new Button("Main Menu");
        panel.addComponent(signupButton);
        panel.addComponent(backButton);

        signupButton.addListener(new Button.Listener() {
            public void onTriggered(Button button) {

                String username = usernameInput.getText();
                String password = passwordInput.getText();
                String userType = userTypeComboBox.getCheckedItem();
                if (userType == null) {
                    Signup.show(channel);
                    return;
                }
                boolean isSignedUp = ApiActions.signup(channel, username, password, userType);
                User user = new User(username, password, userType);

                if (isSignedUp) {
                    if (userType.equals("Client")) {
                        ClientMenu.show(channel, user);
                    } else {
                        DriverMenu.show(channel, user);
                    }
                } else {
                    new MessageDialogBuilder()
                            .setTitle("Error")
                            .setText("Username already in use!")
                            .build()
                            .showDialog(SimpleUber.getInstance().mDialogWindow);
                    Signup.show(channel);
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
