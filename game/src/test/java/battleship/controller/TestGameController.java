package battleship.controller;

import battleship.controller.mode.GameModeStrategy;
import battleship.model.game.FleetManager;
import battleship.model.game.Grid;
import battleship.model.game.Ship;
import battleship.view.BattleshipView;
import it.units.battleship.CellState;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link GameController}.
 * These are intentionally a bit high-level since TurnManager is created internally
 * and is hard to mock cleanly without refactoring the production code.
 */
class TestGameController {

    @Mock
    private Grid gridMock;

    @Mock
    private FleetManager fleetMgrMock;

    @Mock
    private GameModeStrategy modeMock;

    @Mock
    private BattleshipView viewMock;

    private GameController controller; // reused across tests when possible

    @BeforeEach
    void setUp() {
        // Manually opening mocks instead of using an extension
        MockitoAnnotations.openMocks(this);

        // Default stub so constructor logging doesn't explode
        when(modeMock.getModeName()).thenReturn("Test Mode");

        // Mock FleetManager methods needed by BaseGameState.onEnter
        when(fleetMgrMock.getGrid()).thenReturn(gridMock);
        CellState[][] emptyGrid = new CellState[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                emptyGrid[i][j] = CellState.EMPTY;
            }
        }
        when(gridMock.getGrid()).thenReturn(emptyGrid);
        when(fleetMgrMock.getFleet()).thenReturn(java.util.List.of());
        when(fleetMgrMock.getPlacedCounts()).thenReturn(java.util.Map.of());
        when(fleetMgrMock.getRequiredFleetConfiguration()).thenReturn(java.util.Map.of());
    }

    @Test
    void gameController_isCreatedAndHooksViewListeners() {
        controller = new GameController(gridMock, fleetMgrMock, modeMock, viewMock);

        assertNotNull(controller, "Controller should be created");

        // These are side effects of the constructor
        verify(viewMock).setOpponentGridListener(any());
        verify(viewMock).setPlayerGridListener(any());
    }

    @Test
    void startGame_initializesGameModeAndStartsFlow() {
        controller = new GameController(gridMock, fleetMgrMock, modeMock, viewMock);

        controller.startGame();

        // We mainly care that initialize is called with some callback
        verify(modeMock).initialize(any(GameModeStrategy.GameModeCallback.class));

        // This comes indirectly from TurnManager.start()
        // Not ideal to test, but good enough for now
        verify(viewMock).setPlayerTurn(true);
    }

    @Test
    void onOpponentReady_doesNotCrash() {
        controller = new GameController(gridMock, fleetMgrMock, modeMock, viewMock);

        // Currently this method only logs, so just make sure it’s safe
        assertDoesNotThrow(() -> controller.onOpponentReady());
    }

    @Test
    void onShotReceived_acceptsIncomingShot() {
        controller = new GameController(gridMock, fleetMgrMock, modeMock, viewMock);
        Coordinate coord = new Coordinate(0, 0);

        // TurnManager is internal, so we can't verify delegation directly
        assertDoesNotThrow(() -> controller.onShotReceived(coord));
    }

    @Test
    void onGridUpdateReceived_acceptsSerializedGrid() {
        controller = new GameController(gridMock, fleetMgrMock, modeMock, viewMock);

        String fakeGrid = "grid_data"; // not a real grid, but enough for the test
        List<Ship> emptyFleet = List.of();

        assertDoesNotThrow(() ->
                controller.onGridUpdateReceived(fakeGrid, emptyFleet)
        );
    }

    @Test
    void onGameStatusReceived_gameOverDoesNotThrow() {
        controller = new GameController(gridMock, fleetMgrMock, modeMock, viewMock);
        String msg = "Game Over";

        assertDoesNotThrow(() ->
                controller.onGameStatusReceived(GameState.GAME_OVER, msg)
        );
    }

    @Test
    void onGameStatusReceived_activeTurnStateHandled() {
        controller = new GameController(gridMock, fleetMgrMock, modeMock, viewMock);

        assertDoesNotThrow(() ->
                controller.onGameStatusReceived(GameState.ACTIVE_TURN, "Game started")
        );
    }

    @Test
    void onGameStatusReceived_waitingForOpponentHandled() {
        controller = new GameController(gridMock, fleetMgrMock, modeMock, viewMock);

        assertDoesNotThrow(() ->
                controller.onGameStatusReceived(GameState.WAITING_FOR_OPPONENT, "Waiting")
        );
    }

    @Test
    void onConnectionError_showsEndGameScreen() {
        controller = new GameController(gridMock, fleetMgrMock, modeMock, viewMock);
        String errorMsg = "Connection failed";

        controller.onConnectionError(errorMsg);

        // We don't care about the exact message, just that it mentions a connection error
        verify(viewMock).showEndGamePhase(contains("Connection error"));
    }

    @Test
    void controller_canBeCreatedWithoutSpecialFlags() {
        // This test is a bit redundant, but it documents that
        // there is no hidden configuration required.
        controller = new GameController(gridMock, fleetMgrMock, modeMock, viewMock);

        assertNotNull(controller);
    }
}
