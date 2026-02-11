package battleship.ui;

import it.units.battleship.view.welcome.WelcomeUi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestWelcomeUI {

    WelcomeUi welcomeUi;

    @BeforeEach
    public void setUp() {
        welcomeUi = new WelcomeUi();
    }

    @AfterEach
    public void tearDown() {
        if (welcomeUi != null) {
            welcomeUi.dispose();
        }
    }

    @Test
    public void testWelcomeUIInitialization() {
        assertDoesNotThrow(() -> {
            new WelcomeUi();
        });
    }

    @Test
    public void testWelcomeUIActionListeners() {
        assertDoesNotThrow(() -> {
            welcomeUi.onSinglePlayerSelected();
            welcomeUi.onOnlineMultiplayerSelected();
        });
    }

    @Test
    public void testWelcomeUIDisplay() {
        assertDoesNotThrow(() -> {
            welcomeUi.show();
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

    @Test
    public void testGetRootComponent() {
        JComponent rootComponent = welcomeUi.$$$getRootComponent$$$();
        assertNotNull(rootComponent, "Root component should not be null");
        assertTrue(rootComponent instanceof JPanel, "Root component should be a JPanel");
    }

}
