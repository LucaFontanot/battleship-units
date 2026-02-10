package battleship.controller.turn.states;

import battleship.controller.turn.TurnManager;
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

    private WaitingSetupState waitingSetupState;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        waitingSetupState = new WaitingSetupState();
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

        verify(mockTurnManager).setPlayerTurn(false);
        verify(mockTurnManager).notifyUser("Waiting for opponent setup...");
        verify(mockTurnManager).refreshUI();
    }

    @Test
    void testHandleGameStatusReceivedWithActiveTurnTransitionsToActiveState() {
        waitingSetupState.handleGameStatusReceived(mockTurnManager, GameState.ACTIVE_TURN);

        verify(mockTurnManager).transitionToGamePhase();
        verify(mockTurnManager).transitionToActiveTurn();
    }

    @Test
    void testHandleGameStatusReceivedWithWaitingTransitionsToWaitingState() {
        waitingSetupState.handleGameStatusReceived(mockTurnManager, GameState.WAITING_FOR_OPPONENT);

        verify(mockTurnManager).transitionToGamePhase();
        verify(mockTurnManager).transitionToWaitingOpponent();
    }

    @Test
    void testHandleGameStatusReceivedWithGameOverDoesNotTransition() {
        waitingSetupState.handleGameStatusReceived(mockTurnManager, GameState.GAME_OVER);

        verify(mockTurnManager, never()).transitionToActiveTurn();
        verify(mockTurnManager, never()).transitionToWaitingOpponent();
    }

    @Test
    void testHandleGameStatusReceivedWithSetupDoesNotTransition() {
        waitingSetupState.handleGameStatusReceived(mockTurnManager, GameState.SETUP);

        verify(mockTurnManager, never()).transitionToActiveTurn();
        verify(mockTurnManager, never()).transitionToWaitingOpponent();
    }

    @Test
    void testPlayerCannotInteractWithGridsInWaitingSetup() {
        // All grid interaction should do nothing
        waitingSetupState.handlePlayerGridClick(mockTurnManager, null);
        waitingSetupState.handleOpponentGridClick(mockTurnManager, null);
        waitingSetupState.handlePlayerGridHover(mockTurnManager, null);
        waitingSetupState.handleOpponentGridHover(mockTurnManager, null);

        // Verify no transition
        verify(mockTurnManager, never()).transitionToActiveTurn();
        verify(mockTurnManager, never()).transitionToWaitingOpponent();
        verify(mockTurnManager, never()).transitionToWaitingSetup();
    }
}
