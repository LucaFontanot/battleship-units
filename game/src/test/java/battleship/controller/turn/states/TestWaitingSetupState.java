package battleship.controller.turn.states;

import it.units.battleship.GameState;
import it.units.battleship.controller.turn.states.WaitingSetupState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestWaitingSetupState {

    @Mock
    private GameActions mockActions;

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
        waitingSetupState.onEnter(mockActions);

        verify(mockActions).setPlayerTurn(false);
        verify(mockActions).notifyUser("Waiting for opponent setup...");
        verify(mockActions).refreshFleetUI();
    }

    @Test
    void testHandleGameStatusReceivedWithActiveTurnTransitionsToActiveState() {
        waitingSetupState.handleGameStatusReceived(mockActions, GameState.ACTIVE_TURN);

        verify(mockActions).transitionToGamePhase();
        verify(mockActions).transitionToActiveTurn();
    }

    @Test
    void testHandleGameStatusReceivedWithWaitingTransitionsToWaitingState() {
        waitingSetupState.handleGameStatusReceived(mockActions, GameState.WAITING_FOR_OPPONENT);

        verify(mockActions).transitionToGamePhase();
        verify(mockActions).transitionToWaitingOpponent();
    }

    @Test
    void testHandleGameStatusReceivedWithGameOverDoesNotTransition() {
        waitingSetupState.handleGameStatusReceived(mockActions, GameState.GAME_OVER);

        verify(mockActions, never()).transitionToActiveTurn();
        verify(mockActions, never()).transitionToWaitingOpponent();
    }

    @Test
    void testHandleGameStatusReceivedWithSetupDoesNotTransition() {
        waitingSetupState.handleGameStatusReceived(mockActions, GameState.SETUP);

        verify(mockActions, never()).transitionToActiveTurn();
        verify(mockActions, never()).transitionToWaitingOpponent();
    }

    @Test
    void testPlayerCannotInteractWithGridsInWaitingSetup() {
        // All grid interaction should do nothing
        waitingSetupState.handlePlayerGridClick(mockActions, null);
        waitingSetupState.handleOpponentGridClick(mockActions, null);
        waitingSetupState.handlePlayerGridHover(mockActions, null);
        waitingSetupState.handleOpponentGridHover(mockActions, null);

        // Verify no transition
        verify(mockActions, never()).transitionToActiveTurn();
        verify(mockActions, never()).transitionToWaitingOpponent();
        verify(mockActions, never()).transitionToWaitingSetup();
    }
}
