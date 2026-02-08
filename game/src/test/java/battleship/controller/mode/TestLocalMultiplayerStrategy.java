package battleship.controller.mode;

import battleship.controller.handlers.network.NetworkOutputHandler;
import battleship.controller.network.CommunicationEvents;
import battleship.controller.network.NetworkClient;
import battleship.model.Grid;
import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TestLocalMultiplayerStrategy {

    @Mock
    private GameModeStrategy.GameModeCallback mockCallback;

    private LocalMultiplayerStrategy localMultiplayerStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetModeNameReturnsLocalMultiplayerHost() {
        localMultiplayerStrategy = new LocalMultiplayerStrategy("ws://localhost:8080/game", true);
        assertEquals("Local Multiplayer (Host)", localMultiplayerStrategy.getModeName());
    }

    @Test
    void testGetModeNameReturnsLocalMultiplayerGuest() {
        localMultiplayerStrategy = new LocalMultiplayerStrategy("ws://localhost:8080/game", false);
        assertEquals("Local Multiplayer (Guest)", localMultiplayerStrategy.getModeName());
    }

    @Test
    void testIsHostReturnsTrue() {
        localMultiplayerStrategy = new LocalMultiplayerStrategy("ws://localhost:8080/game", true);
        assertTrue(localMultiplayerStrategy.isHost());
    }

    @Test
    void testIsHostReturnsFalse() {
        localMultiplayerStrategy = new LocalMultiplayerStrategy("ws://localhost:8080/game", false);
        assertFalse(localMultiplayerStrategy.isHost());
    }

    @Test
    void testInitializeWithInvalidUriDoesNotThrow() {
        localMultiplayerStrategy = new LocalMultiplayerStrategy("invalid-uri", true);

        // Initialize handles connection errors internally
        assertDoesNotThrow(() -> localMultiplayerStrategy.initialize(mockCallback));
    }

    @Test
    void testShutdownDoesNotThrow() {
        localMultiplayerStrategy = new LocalMultiplayerStrategy("ws://localhost:8080/game", true);

        assertDoesNotThrow(() -> localMultiplayerStrategy.shutdown());
    }
}
