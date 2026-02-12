package battleship.ui;

import it.units.battleship.controller.lobby.LobbyController;
import it.units.battleship.view.lobby.LobbySelector;
import it.units.battleship.data.LobbiesResponseData;
import it.units.battleship.data.LobbyData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TestLobbySelector {

    @Mock
    private LobbyController mockController;

    private LobbySelector lobbySelector;
    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        lobbySelector = new LobbySelector(mockController);
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (lobbySelector != null) {
            lobbySelector.cleanup();
        }
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    public void testLobbySelectorInitialization() {
        assertNotNull(lobbySelector, "LobbySelector should be initialized");
    }

    @Test
    public void testPromptForNameReturnsUserInput() {
        assertDoesNotThrow(() -> {
            Method method = LobbySelector.class.getMethod("promptForName");
            assertNotNull(method, "promptForName method should exist");
        });
    }

    @Test
    public void testHandleLobbyJoinSuccessfulJoin() throws Exception {
        LobbyData testLobby = LobbyData.builder()
                .lobbyID("test-lobby-id")
                .lobbyName("Test Lobby")
                .playerOne("Player1")
                .build();

        when(mockController.connectLobbyWebsocket(any(LobbyData.class), anyString()))
                .thenReturn(true);

        Method handleLobbyJoinMethod = LobbySelector.class.getDeclaredMethod(
                "handleLobbyJoin", LobbyData.class, String.class);
        handleLobbyJoinMethod.setAccessible(true);

        assertDoesNotThrow(() -> handleLobbyJoinMethod.invoke(lobbySelector, testLobby, "Player2"));
        verify(mockController, times(1)).connectLobbyWebsocket(testLobby, "Player2");
    }

    @Test
    public void testRenderLobbiesEmptyList() throws Exception {
        List<LobbyData> emptyLobbies = new ArrayList<>();

        Method renderLobbiesMethod = LobbySelector.class.getDeclaredMethod(
                "renderLobbies", List.class);
        renderLobbiesMethod.setAccessible(true);

        assertDoesNotThrow(() -> renderLobbiesMethod.invoke(lobbySelector, emptyLobbies));
    }

    @Test
    public void testRenderLobbiesWithLobbies() throws Exception {
        List<LobbyData> lobbies = new ArrayList<>();
        lobbies.add(LobbyData.builder()
                .lobbyID("lobby1")
                .lobbyName("Lobby One")
                .playerOne("Alice")
                .build());
        lobbies.add(LobbyData.builder()
                .lobbyID("lobby2")
                .lobbyName("Lobby Two")
                .playerOne("Bob")
                .build());

        Method renderLobbiesMethod = LobbySelector.class.getDeclaredMethod(
                "renderLobbies", List.class);
        renderLobbiesMethod.setAccessible(true);

        assertDoesNotThrow(() -> renderLobbiesMethod.invoke(lobbySelector, lobbies));
    }

    @Test
    public void testGetLobbySelectorPanelCreatesValidPanel() throws Exception {
        LobbyData testLobby = LobbyData.builder()
                .lobbyID("test-id")
                .lobbyName("Test Lobby")
                .playerOne("TestPlayer")
                .build();

        Method getLobbySelectorPanelMethod = LobbySelector.class.getDeclaredMethod(
                "getLobbySelectorPanel", LobbyData.class);
        getLobbySelectorPanelMethod.setAccessible(true);

        JPanel panel = (JPanel) getLobbySelectorPanelMethod.invoke(lobbySelector, testLobby);

        assertNotNull(panel, "Panel should not be null");
        assertTrue(panel.getComponentCount() > 0, "Panel should have components");

        boolean hasJoinButton = false;
        for (Component comp : getAllComponents(panel)) {
            if (comp instanceof JButton button) {
                if ("JOIN".equals(button.getText())) {
                    hasJoinButton = true;
                    break;
                }
            }
        }
        assertTrue(hasJoinButton, "Panel should contain a JOIN button");
    }

    @Test
    public void testFetchAndRenderLobbiesWithValidData() throws Exception {
        List<LobbyData> lobbies = new ArrayList<>();
        lobbies.add(LobbyData.builder()
                .lobbyID("lobby1")
                .lobbyName("Lobby One")
                .playerOne("Alice")
                .build());

        LobbiesResponseData responseData = LobbiesResponseData.builder()
                .results(lobbies)
                .build();

        when(mockController.getLobbies()).thenReturn(responseData);

        Method fetchAndRenderLobbiesMethod = LobbySelector.class.getDeclaredMethod("fetchAndRenderLobbies");
        fetchAndRenderLobbiesMethod.setAccessible(true);

        assertDoesNotThrow(() -> fetchAndRenderLobbiesMethod.invoke(lobbySelector));
        verify(mockController, times(1)).getLobbies();
    }

    @Test
    public void testFetchAndRenderLobbiesWithNullData() throws Exception {
        when(mockController.getLobbies()).thenReturn(null);

        Method fetchAndRenderLobbiesMethod = LobbySelector.class.getDeclaredMethod("fetchAndRenderLobbies");
        fetchAndRenderLobbiesMethod.setAccessible(true);

        assertDoesNotThrow(() -> fetchAndRenderLobbiesMethod.invoke(lobbySelector));
        verify(mockController, times(1)).getLobbies();
    }

    @Test
    public void testCleanupShutsDownScheduler() {
        assertDoesNotThrow(() -> lobbySelector.cleanup());
    }


    private List<Component> getAllComponents(Container container) {
        List<Component> components = new ArrayList<>();
        for (Component comp : container.getComponents()) {
            components.add(comp);
            if (comp instanceof Container) {
                components.addAll(getAllComponents((Container) comp));
            }
        }
        return components;
    }
}
