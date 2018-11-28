package Client.Views.Client;

import Client.Api.ApiActions;
import Client.Api.UberApi;
import Client.SimpleUber;
import Client.Utils.ThreadChannel;
import Client.Views.Login;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.WaitingDialog;
import com.googlecode.lanterna.screen.Screen;

public class CallUberScreen {

    public static void show(final ThreadChannel channel) {
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
                SimpleUber simpleUber = SimpleUber.getInstance();

                WaitingDialog waiting = WaitingDialog.createDialog("Finding Uber...",
                        "Please wait while we find you a driver.");
                waiting.showDialog(simpleUber.mDialogWindow,
                        true);

                boolean isUberCalled = ApiActions.callUber(channel,
                        start.getText(),
                        destination.getText());

                if (isUberCalled) {

                    waiting.close();
                    String driverUsername = "Rajesh";
                    UberInTransitScreen.show(channel, driverUsername);
                    ApiActions.waitForEndTrip(channel);
                    StarRatingScreen.show(channel, driverUsername);
                } else {
                    new MessageDialogBuilder()
                            .setTitle("Error")
                            .setText("An error occured communicating with the server. Please try again!")
                            .build()
                            .showDialog(SimpleUber.getInstance().mDialogWindow);
                    ClientMenu.show(channel);
                }
            }
        }).addTo(panel);

        new Button("Cancel", new Runnable() {
            public void run() {
                ClientMenu.show(channel);
                return;
            }
        }).addTo(panel);

        // Add panel to window
        window.setComponent(panel);
    }
}
