package battleship.controller.turn.states;

import battleship.controller.mode.GameModeStrategy;
import battleship.controller.turn.TurnManager;
import battleship.model.FleetManager;
import battleship.model.Grid;
import battleship.view.core.BattleshipView;
import it.units.battleship.CellState;
import it.units.battleship.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestWaitingSetupState {

    @Mock
    private TurnManager mockTurnManager;
    @Mock
    private BattleshipView mockView;
    @Mock
    private GameModeStrategy mockGameModeStrategy;
    @Mock
    private FleetManager mockFleetManager;
    @Mock
    private Grid mockGrid;

    private WaitingSetupState waitingSetupState;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        waitingSetupState = new WaitingSetupState();

        when(mockTurnManager.getView()).thenReturn(mockView);
        when(mockTurnManager.getGameModeStrategy()).thenReturn(mockGameModeStrategy);
        when(mockTurnManager.getFleetManager()).thenReturn(mockFleetManager);
        when(mockFleetManager.getGrid()).thenReturn(mockGrid);
        CellState[][] emptyGrid = new CellState[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                emptyGrid[i][j] = CellState.EMPTY;
            }
        }
        when(mockGrid.getGrid()).thenReturn(emptyGrid);
        when(mockFleetManager.getFleet()).thenReturn(java.util.List.of());
        when(mockFleetManager.getPlacedCounts()).thenReturn(java.util.Map.of());
        when(mockFleetManager.getRequiredFleetConfiguration()).thenReturn(java.util.Map.of());
    }

    @Test
    void testGetStateNameReturnsWaitingSetup() {
        assertEquals("WAITING_SETUP", waitingSetupState.getStateName());
    }

    @Test
    void testCanShootReturnsFalse() {
        assertFalse(waitingSetupState.canShoot());
    }

    @Test
    void testCanPlaceShipReturnsFalse() {
        assertFalse(waitingSetupState.canPlaceShip());
    }

    @Test
    void testOnEnterDisablesInteractionsAndShowsMessage() {
        waitingSetupState.onEnter(mockTurnManager);

        verify(mockView).setPlayerTurn(false);
        verify(mockView).showSystemMessage("Waiting for opponent setup...");
    }

    @Test
    void testHandleGameStatusReceivedWithActiveTurnTransitionsToActiveState() {
        when(mockTurnManager.getCurrentStateName()).thenReturn("WAITING_SETUP");

        waitingSetupState.handleGameStatusReceived(mockTurnManager, GameState.ACTIVE_TURN);

        verify(mockTurnManager).transitionTo(any(ActiveTurnState.class));
    }

    @Test
    void testHandleGameStatusReceivedWithWaitingTransitionsToWaitingState() {
        when(mockTurnManager.getCurrentStateName()).thenReturn("WAITING_SETUP");

        waitingSetupState.handleGameStatusReceived(mockTurnManager, GameState.WAITING_FOR_OPPONENT);

        verify(mockTurnManager).transitionTo(any(WaitingOpponentState.class));
    }

    @Test
    void testHandleGameStatusReceivedWithGameOverDoesNotTransition() {
        waitingSetupState.handleGameStatusReceived(mockTurnManager, GameState.GAME_OVER);

        verify(mockTurnManager, never()).transitionTo(any());
    }

    @Test
    void testHandleGameStatusReceivedWithSetupDoesNotTransition() {
        waitingSetupState.handleGameStatusReceived(mockTurnManager, GameState.SETUP);

        verify(mockTurnManager, never()).transitionTo(any());
    }

    @Test
    void testPlayerCannotInteractWithGridsInWaitingSetup() {
        // All grid interactions should do nothing
        waitingSetupState.handlePlayerGridClick(mockTurnManager, null);
        waitingSetupState.handleOpponentGridClick(mockTurnManager, null);
        waitingSetupState.handlePlayerGridHover(mockTurnManager, null);
        waitingSetupState.handleOpponentGridHover(mockTurnManager, null);

        // Verify no transitions or view updates
        verify(mockTurnManager, never()).transitionTo(any());
        verify(mockView, never()).updatePlayerGrid(any(), any());
        verify(mockView, never()).updateOpponentGrid(any(), any());
    }
}
