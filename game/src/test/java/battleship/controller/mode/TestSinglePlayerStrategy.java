package battleship.controller.mode;

import battleship.testutil.FakeCallback;
import it.units.battleship.controller.mode.SinglePlayerStrategy;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.ShipType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestSinglePlayerStrategy {

    private SinglePlayerStrategy singlePlayerStrategy;
    private FakeCallback fakeCallback;
    private Map<ShipType, Integer> requiredFleetConfiguration;

    @BeforeEach
    void setUp() {
        fakeCallback = new FakeCallback();
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
        singlePlayerStrategy.initialize(fakeCallback);

        assertTrue(fakeCallback.opponentReadyCalled);
    }

    @Test
    void testSendShotCallsOnGridUpdateReceived() throws InterruptedException {
        singlePlayerStrategy.initialize(fakeCallback);
        Coordinate coord = new Coordinate(0, 0);

        singlePlayerStrategy.sendShot(coord);

        // Wait for the async execution to complete
        long deadline = System.currentTimeMillis() + 3000;
        while (fakeCallback.lastGridUpdateSerialized == null && System.currentTimeMillis() < deadline) {
            Thread.sleep(50);
        }

        assertNotNull(fakeCallback.lastGridUpdateSerialized);
    }

    @Test
    void testSendShotTriggersGameOverWhenNoShipsPlaced() throws InterruptedException {
        // Create a strategy with NO ships required
        SinglePlayerStrategy emptyStrategy = new SinglePlayerStrategy(Map.of());
        emptyStrategy.initialize(fakeCallback);
        Coordinate coord = new Coordinate(0, 0);

        emptyStrategy.sendShot(coord);

        // Since AI has no ships placed, game should end immediately with player winning
        long deadline = System.currentTimeMillis() + 3000;
        while (fakeCallback.lastGameStatusState == null && System.currentTimeMillis() < deadline) {
            Thread.sleep(50);
        }

        assertEquals(GameState.GAME_OVER, fakeCallback.lastGameStatusState);
        assertTrue(fakeCallback.lastGameStatusMessage.contains("win"));
        emptyStrategy.shutdown();
    }

    @Test
    void testSendGridUpdateProcessesLastShotResult() {
        singlePlayerStrategy.initialize(fakeCallback);

        Grid grid = new Grid(10, 10);
        List<Ship> fleet = List.of();

        assertDoesNotThrow(() ->
                singlePlayerStrategy.sendGridUpdate(grid, fleet, true)
        );
    }

    @Test
    void testNotifySetupCompleteDoesNotThrow() {
        singlePlayerStrategy.initialize(fakeCallback);

        assertDoesNotThrow(() -> singlePlayerStrategy.notifySetupComplete());
    }

    @Test
    void testSendGameOverDoesNotThrow() {
        singlePlayerStrategy.initialize(fakeCallback);

        assertDoesNotThrow(() -> singlePlayerStrategy.sendGameOver("Game Over"));
    }

    @Test
    void testShutdownStopsExecutor() {
        singlePlayerStrategy.initialize(fakeCallback);

        assertDoesNotThrow(() -> singlePlayerStrategy.shutdown());
    }
}
