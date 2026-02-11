package battleship.controller;

import it.units.battleship.controller.GameController;
import it.units.battleship.controller.mode.GameModeStrategy;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.view.core.BattleshipView;
import it.units.battleship.CellState;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TestGameController {

    @Mock
    private Grid gridMock;

    @Mock
    private FleetManager fleetMgrMock;

    @Mock
    private GameModeStrategy modeMock;

    @Mock
    private BattleshipView viewMock;

    private GameController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(modeMock.getModeName()).thenReturn("Test Mode");

        when(fleetMgrMock.getGrid()).thenReturn(gridMock);
        CellState[][] emptyGrid = new CellState[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                emptyGrid[i][j] = CellState.EMPTY;
            }
        }
        when(gridMock.getGrid()).thenReturn(emptyGrid);
        when(fleetMgrMock.getGridRows()).thenReturn(10);
        when(fleetMgrMock.getGridCols()).thenReturn(10);
        when(gridMock.getRow()).thenReturn(10);
        when(gridMock.getCol()).thenReturn(10);
        when(fleetMgrMock.getFleet()).thenReturn(List.of());
        when(fleetMgrMock.getPlacedCounts()).thenReturn(Map.of());
        when(fleetMgrMock.getRequiredFleetConfiguration()).thenReturn(Map.of());
        when(fleetMgrMock.getSerializedGridState()).thenReturn("0".repeat(100));

        controller = new GameController(fleetMgrMock, modeMock, viewMock);
    }

    // ===== Constructor and lifecycle =====

    @Test
    void gameController_isCreatedAndHooksViewListeners() {
        assertNotNull(controller);
        verify(viewMock).setOpponentGridListener(any());
        verify(viewMock).setPlayerGridListener(any());
    }

    @Test
    void startGame_initializesGameModeAndStartsFlow() {
        controller.startGame();

        verify(modeMock).initialize(any(GameModeStrategy.GameModeCallback.class));
        verify(viewMock).setPlayerTurn(true);
    }

    // ===== GameModeCallback =====

    @Test
    void onOpponentReady_doesNotCrash() {
        assertDoesNotThrow(() -> controller.onOpponentReady());
    }

    @Test
    void onShotReceived_acceptsIncomingShot() {
        Coordinate coord = new Coordinate(0, 0);
        assertDoesNotThrow(() -> controller.onShotReceived(coord));
    }

    @Test
    void onGridUpdateReceived_acceptsSerializedGrid() {
        String fakeGrid = "0".repeat(100);
        List<Ship> emptyFleet = List.of();

        assertDoesNotThrow(() -> controller.onGridUpdateReceived(fakeGrid, emptyFleet));
    }

    @Test
    void onGameStatusReceived_gameOverDoesNotThrow() {
        assertDoesNotThrow(() -> controller.onGameStatusReceived(GameState.GAME_OVER, "Game Over"));
    }

    @Test
    void onGameStatusReceived_activeTurnStateHandled() {
        assertDoesNotThrow(() -> controller.onGameStatusReceived(GameState.ACTIVE_TURN, "Game started"));
    }

    @Test
    void onGameStatusReceived_waitingForOpponentHandled() {
        assertDoesNotThrow(() -> controller.onGameStatusReceived(GameState.WAITING_FOR_OPPONENT, "Waiting"));
    }

    @Test
    void onConnectionError_showsEndGameScreen() {
        controller.onConnectionError("Connection failed");
        verify(viewMock).showEndGamePhase(contains("Connection error"));
    }

    // ===== GameActions: view delegation =====

    @Test
    void setPlayerTurn_delegatesToView() {
        controller.setPlayerTurn(true);
        verify(viewMock).setPlayerTurn(true);
    }

    @Test
    void notifyUser_delegatesToView() {
        controller.notifyUser("Hello");
        verify(viewMock).showSystemMessage("Hello");
    }

    @Test
    void refreshPlayerGrid_updatesViewWithModelState() {
        controller.refreshPlayerGrid();

        verify(fleetMgrMock).getSerializedGridState();
        verify(fleetMgrMock).getFleet();
        verify(viewMock).updatePlayerGrid(anyString(), anyList());
    }

    @Test
    void refreshFleetUI_updatesViewWithFleetCounts() {
        controller.refreshFleetUI();

        verify(fleetMgrMock).getPlacedCounts();
        verify(fleetMgrMock).getRequiredFleetConfiguration();
        verify(viewMock).refreshFleetSelection(anyMap(), anyMap());
    }

    @Test
    void showShotPreview_delegatesToView() {
        Coordinate coord = new Coordinate(3, 5);
        controller.showShotPreview(coord);
        verify(viewMock).showShotPreview(coord);
    }

    @Test
    void showEndGame_delegatesToView() {
        controller.showEndGame("Game Over!");
        verify(viewMock).showEndGamePhase("Game Over!");
    }

    @Test
    void transitionToGamePhase_delegatesToView() {
        controller.transitionToGamePhase();
        verify(viewMock).transitionToGamePhase();
    }

    @Test
    void playerErrorSound_delegatesToView() {
        controller.playerErrorSound();
        verify(viewMock).playerErrorSound();
    }

    // ===== GameActions: network =====

    @Test
    void fireShot_sendsShotViaGameMode() {
        Coordinate coord = new Coordinate(2, 3);
        controller.fireShot(coord);
        verify(modeMock).sendShot(coord);
    }

    @Test
    void sendGameOver_delegatesToGameMode() {
        controller.sendGameOver("Game over message");
        verify(modeMock).sendGameOver("Game over message");
    }

    // ===== GameActions: model query =====

    @Test
    void getOpponentCellState_returnsEmptyForNewGrid() {
        Coordinate coord = new Coordinate(0, 0);
        assertEquals(CellState.EMPTY, controller.getOpponentCellState(coord));
    }

    // ===== GameActions: processIncomingShot =====

    @Test
    void processIncomingShot_updatesModelAndSendsGridUpdate() {
        Coordinate coord = new Coordinate(0, 0);
        when(fleetMgrMock.handleIncomingShot(coord)).thenReturn(true);
        when(fleetMgrMock.isGameOver()).thenReturn(false);

        boolean gameOver = controller.processIncomingShot(coord);

        assertFalse(gameOver);
        verify(fleetMgrMock).handleIncomingShot(coord);
        verify(modeMock).sendGridUpdate(eq(gridMock), anyList(), eq(true));
        verify(viewMock).updatePlayerGrid(anyString(), anyList());
    }

    @Test
    void processIncomingShot_returnsTrueWhenGameOver() {
        Coordinate coord = new Coordinate(0, 0);
        when(fleetMgrMock.handleIncomingShot(coord)).thenReturn(true);
        when(fleetMgrMock.isGameOver()).thenReturn(true);

        boolean gameOver = controller.processIncomingShot(coord);

        assertTrue(gameOver);
    }

    // ===== GameActions: placeShip =====

    @Test
    void placeShip_withNullShipType_doesNothing() {
        Coordinate coord = new Coordinate(0, 0);
        when(viewMock.getSelectedShipType()).thenReturn(null);
        when(viewMock.getSelectedOrientation()).thenReturn(Orientation.HORIZONTAL_RIGHT);

        controller.placeShip(coord);

        verify(fleetMgrMock, never()).addShip(any());
    }

    // ===== GameInteractionFacade =====

    @Test
    void requestShot_delegatesToTurnManager() {
        Coordinate coord = new Coordinate(0, 0);
        // Just verify it doesn't crash — the state machine handles the rest
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
}
