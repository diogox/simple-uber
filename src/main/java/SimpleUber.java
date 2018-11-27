import Views.InitialScreen;
import Views.Signup;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class SimpleUber {

    public static void main(String[] args) throws IOException {

        // Setup terminal and screen layers
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();

        InitialScreen.show(screen, window);

        window.setHints(Arrays.asList(Window.Hint.CENTERED));
        SimpleTheme theme = SimpleTheme.makeTheme(
                true,
                TextColor.ANSI.BLACK,
                TextColor.ANSI.WHITE,
                TextColor.ANSI.DEFAULT,
                TextColor.ANSI.DEFAULT,
                TextColor.ANSI.DEFAULT,
                TextColor.ANSI.DEFAULT,
                TextColor.ANSI.DEFAULT
        );
        window.setTheme(theme);

        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));
        gui.addWindowAndWait(window);
    }
}
