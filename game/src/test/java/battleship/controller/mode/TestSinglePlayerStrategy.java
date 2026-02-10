package battleship.controller.mode;

import battleship.model.Grid;
import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.ShipType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestSinglePlayerStrategy {

    private SinglePlayerStrategy singlePlayerStrategy;
    private GameModeStrategy.GameModeCallback mockCallback;
    private Map<ShipType, Integer> requiredFleetConfiguration;

    @BeforeEach
    void setUp() {
        mockCallback = mock(GameModeStrategy.GameModeCallback.class);
        requiredFleetConfiguration = Map.of(
                ShipType.CARRIER, 1,
                ShipType.BATTLESHIP, 1,
                ShipType.CRUISER, 1,
                ShipType.FRIGATE, 1,
                ShipType.DESTROYER, 1
        );
        singlePlayerStrategy = new SinglePlayerStrategy(requiredFleetConfiguration);
    }

    @AfterEach
    void tearDown() {
        if (singlePlayerStrategy != null) {
            singlePlayerStrategy.shutdown();
        }
    }

    @Test
    void testGetModeNameReturnsSinglePlayer() {
        assertEquals("Single Player", singlePlayerStrategy.getModeName());
    }

    @Test
    void testInitializeCallsOnOpponentReady() {
        singlePlayerStrategy.initialize(mockCallback);

        verify(mockCallback).onOpponentReady();
    }

    @Test
    void testSendShotCallsOnGridUpdateReceived() {
        singlePlayerStrategy.initialize(mockCallback);
        Coordinate coord = new Coordinate(0, 0);

        singlePlayerStrategy.sendShot(coord);

        // Verify with timeout for async execution
        verify(mockCallback, timeout(2000)).onGridUpdateReceived(anyString(), anyList());
    }

    @Test
    void testSendShotTriggersGameOverWhenNoShipsPlaced() {
        // Create a strategy with NO ships required
        SinglePlayerStrategy emptyStrategy = new SinglePlayerStrategy(Map.of());
        emptyStrategy.initialize(mockCallback);
        Coordinate coord = new Coordinate(0, 0);

        emptyStrategy.sendShot(coord);

        // Since AI has no ships placed, game should end immediately with player winning
        verify(mockCallback, timeout(2000)).onGameStatusReceived(
                eq(GameState.GAME_OVER),
                contains("win")
        );
        emptyStrategy.shutdown();
    }

    @Test
    void testSendGridUpdateProcessesLastShotResult() {
        singlePlayerStrategy.initialize(mockCallback);

        Grid mockGrid = mock(Grid.class);
        List<Ship> fleet = List.of();

        assertDoesNotThrow(() ->
                singlePlayerStrategy.sendGridUpdate(mockGrid, fleet, true)
        );
    }

    @Test
    void testNotifySetupCompleteDoesNotThrow() {
        singlePlayerStrategy.initialize(mockCallback);

        assertDoesNotThrow(() -> singlePlayerStrategy.notifySetupComplete());
    }

    @Test
    void testSendGameOverDoesNotThrow() {
        singlePlayerStrategy.initialize(mockCallback);

        assertDoesNotThrow(() -> singlePlayerStrategy.sendGameOver("Game Over"));
    }

    @Test
    void testShutdownStopsExecutor() {
        singlePlayerStrategy.initialize(mockCallback);

        assertDoesNotThrow(() -> singlePlayerStrategy.shutdown());
    }
}
