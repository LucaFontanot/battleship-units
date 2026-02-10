package battleship.ui;

import battleship.view.welcome.WelcomeUi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class WelcomeUI {
    @Test
    public void testWelcomeUIInitialization() {
        assertDoesNotThrow(() -> {
            new WelcomeUi();
        });
    }

    @Test
    public void testWelcomeUIActionListeners() {
        WelcomeUi welcomeUI = new WelcomeUi();
        assertDoesNotThrow(() -> {
            welcomeUI.onSinglePlayerSelected();
            welcomeUI.onOnlineMultiplayerSelected();
        });
    }

    @Test
    public void testWelcomeUIDisplay() {
        WelcomeUi welcomeUI = new WelcomeUi();
        assertDoesNotThrow(() -> {
            welcomeUI.show();
            Thread.sleep(1000); // Allow time for the UI to render
        });
    }

    @Test
    public void testThemeSelection() {
        WelcomeUi welcomeUI = new WelcomeUi();
        welcomeUI.show();
        assertDoesNotThrow(() -> {
            welcomeUI.onThemeAutoSelected();
            Thread.sleep(1000);
            welcomeUI.onThemeLightSelected();
            Thread.sleep(1000);
            welcomeUI.onThemeDarkSelected();
            Thread.sleep(1000);
        });
    }
}
