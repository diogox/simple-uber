package Client;

import Client.Api.ApiActions;
import Client.Api.UberApi;
import Client.Utils.ThreadChannel;
import Client.Views.InitialScreen;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static Shared.Models.Actions.*;

public class SimpleUber {
    private static SimpleUber mInstance = null;
    public final Screen mScreen;
    public final WindowBasedTextGUI mDialogWindow;
    public final MultiWindowTextGUI mGui;
    public final BasicWindow mWindow;
    public final SwingTerminalFrame mTerminal;
    public final SimpleTheme mTheme;

    private SimpleUber(ThreadChannel channel) throws IOException {
        UberApi api = new UberApi(channel);
        new Thread(api).start();

        // Setup terminal and screen layers
        mTerminal = (SwingTerminalFrame) new DefaultTerminalFactory().createTerminal();
        mTerminal.setTitle("Simple Uber");

        mScreen = new TerminalScreen(mTerminal);
        mScreen.startScreen();

        // Create window to hold the panel
        mWindow = new BasicWindow();
        mDialogWindow = new MultiWindowTextGUI(mScreen);

        mWindow.setHints(Arrays.asList(Window.Hint.CENTERED));
        mTheme = SimpleTheme.makeTheme(
                true,
                TextColor.ANSI.BLACK,
                TextColor.ANSI.WHITE,
                TextColor.ANSI.DEFAULT,
                TextColor.ANSI.DEFAULT,
                TextColor.ANSI.DEFAULT,
                TextColor.ANSI.DEFAULT,
                TextColor.ANSI.DEFAULT
        );

        // Create gui and start gui
        mGui = new MultiWindowTextGUI(mScreen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));
    }

    public static void main(String[] args) throws IOException {
        final ThreadChannel channel = new ThreadChannel();
        mInstance = new SimpleUber(channel);

        InitialScreen.show(channel);

        mInstance.mWindow.setTheme(mInstance.mTheme);
        mInstance.mDialogWindow.setTheme(mInstance.mTheme);

        mInstance.mTerminal.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                channel.sendToOtherThread(ACTION_QUIT, new ArrayList<String>());
                e.getWindow().dispose();
            }
        });

        mInstance.mGui.addWindowAndWait(mInstance.mWindow);
    }

    public static SimpleUber getInstance() {
        return mInstance;
    }
}
