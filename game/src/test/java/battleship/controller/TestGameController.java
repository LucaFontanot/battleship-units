package battleship.controller;

import battleship.testutil.FakeGameMode;
import battleship.testutil.FakeView;
import it.units.battleship.controller.GameController;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestGameController {

    private FakeView fakeView;
    private FakeGameMode fakeGameMode;
    private FleetManager fleetManager;
    private Grid playerGrid;
    private GameController controller;

    @BeforeEach
    void setUp() {
        fakeView = new FakeView();
        fakeGameMode = new FakeGameMode();
        playerGrid = new Grid(10, 10);
        fleetManager = new FleetManager(playerGrid, Map.of(ShipType.DESTROYER, 2));
        controller = new GameController(fleetManager, fakeGameMode, fakeView);
    }

    // ===== Constructor and lifecycle =====

    @Test
    void gameController_isCreatedAndHooksViewListeners() {
        assertNotNull(controller);
        // Both grid listeners should be setted
        assertNotNull(fakeView.playerGridListener);
        assertNotNull(fakeView.opponentGridListener);
    }

    @Test
    void startGame_initializesGameModeAndStartFlow() {
        controller.startGame();

        // View was opened and game mode was initialized with the callback
        assertTrue(fakeView.opened);
        assertNotNull(fakeGameMode.lastCallback);
        assertTrue(fakeView.playerTurn);
    }

    // ===== GameModeCallback =====

    @Test
    void onOpponentReady_doesNotCrash() {
        assertDoesNotThrow(() -> controller.onOpponentReady());
    }

    @Test
    void onShotReceived_delegatesToTurnManager() {
        Coordinate coord = new Coordinate(0, 0);
        // Should not throw even if no ships is placed
        assertDoesNotThrow(() -> controller.onShotReceived(coord));
    }

    @Test
    void onGridUpdateReceived_updatesOpponentGrid() {
        String gridData = "0".repeat(100);
        List<Ship> emptyFleet = List.of();

        controller.onGridUpdateReceived(gridData, emptyFleet);

        // The view should have received the opponent grid update
        assertEquals(gridData, fakeView.lastOpponentGridSerialized);
        assertEquals(emptyFleet, fakeView.lastOpponentFleet);
    }

    @Test
    void onGameStatusReceived_gameOver_triggersGameOverState() {
        assertDoesNotThrow(() -> controller.onGameStatusReceived(GameState.GAME_OVER, "Game Over"));
        // The end game message should have been displayed
        assertNotNull(fakeView.lastEndGameMessage);
    }

    @Test
    void onGameStatusReceived_activeTurn_doesNotThrow() {
        assertDoesNotThrow(() -> controller.onGameStatusReceived(GameState.ACTIVE_TURN, "Game started"));
    }

    @Test
    void onGameStatusReceived_waitingForOpponent_doesNotThrow() {
        assertDoesNotThrow(() -> controller.onGameStatusReceived(GameState.WAITING_FOR_OPPONENT, "Waiting"));
    }

    @Test
    void onConnectionError_showsEndGameScreen() {
        controller.onConnectionError("Connection failed");

        // The view should shows the connection error message
        assertNotNull(fakeView.lastEndGameMessage);
        assertTrue(fakeView.lastEndGameMessage.contains("Connection error"));
    }

    // ===== GameInteractionFacade =====

    @Test
    void requestShot_delegatesToTurnManager() {
        Coordinate coord = new Coordinate(0, 0);
        assertDoesNotThrow(() -> controller.requestShot(coord));
    }

    @Test
    void requestShipPlacement_delegatesToTurnManager() {
        Coordinate coord = new Coordinate(0, 0);
        assertDoesNotThrow(() -> controller.requestShipPlacement(coord));
    }

    @Test
    void requestPlacementPreview_delegatesToTurnManager() {
        Coordinate coord = new Coordinate(0, 0);
        assertDoesNotThrow(() -> controller.requestPlacementPreview(coord));
    }

    @Test
    void previewShot_delegatesToTurnManager() {
        Coordinate coord = new Coordinate(0, 0);
        assertDoesNotThrow(() -> controller.previewShot(coord));
    }

    // ===== Integration: full ship placement flow through the controller =====

    @Test
    void requestShipPlacement_actuallyPlacesShipOnGrid() {
        fakeView.selectedShipType = ShipType.DESTROYER;
        fakeView.selectedOrientation = Orientation.HORIZONTAL_RIGHT;

        controller.startGame();
        controller.requestShipPlacement(new Coordinate(0, 0));

        // The ship should have been placed in the real FleetManager
        assertEquals(1, fleetManager.getFleet().size());
    }
}
