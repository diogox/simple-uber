package Client.Views.Client;

import Client.Api.ApiActions;
import Client.SimpleUber;
import Client.Utils.ThreadChannel;
import Shared.Models.Ride;
import Shared.Models.User;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.screen.Screen;

public class CallUberScreen {

    public static void show(final ThreadChannel channel, final User user) {
        Screen screen = SimpleUber.getInstance().mScreen;
        Window window = SimpleUber.getInstance().mWindow;
        screen.clear();

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        Label startLabel = new Label("Start");
        final TextBox start= new TextBox();
        panel.addComponent(startLabel);
        panel.addComponent(start);

        Label destinationLabel = new Label("Destination");
        final TextBox destination = new TextBox();
        panel.addComponent(destinationLabel);
        panel.addComponent(destination);

        panel.addComponent(new EmptySpace(new TerminalSize(1,1)));
        panel.addComponent(new EmptySpace(new TerminalSize(1,1)));

        new Button("Confirm", new Runnable() {
            public void run() {

                String driverUsername = ApiActions.callUber(channel,
                        user.getUsername(),
                        start.getText(),
                        destination.getText());

                if (driverUsername != null) {
                    Ride ride = new Ride(start.getText(), destination.getText());
                    ride.setClientUsername(user.getUsername());
                    ride.setDriverUsername(driverUsername);
                    UberInTransitScreen.show(channel, ride);
                } else {
                    new MessageDialogBuilder()
                            .setTitle("Error")
                            .setText("An error occured communicating with the server. Please try again!")
                            .build()
                            .showDialog(SimpleUber.getInstance().mDialogWindow);
                    ClientMenu.show(channel, user);
                }
            }
        }).addTo(panel);

        new Button("Cancel", new Runnable() {
            public void run() {
                ClientMenu.show(channel, user);
            }
        }).addTo(panel);

        // Add panel to window
        window.setComponent(panel);
    }
}
