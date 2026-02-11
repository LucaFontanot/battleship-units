package battleship.controller.turn.states;

import it.units.battleship.CellState;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.controller.turn.states.ActiveTurnState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestActiveTurnState {

    @Mock
    private GameActions mockActions;

    private ActiveTurnState activeTurnState;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        activeTurnState = new ActiveTurnState();
    }

    @Test
    void testGetStateNameReturnsActiveTurn() {
        assertEquals(GameState.ACTIVE_TURN.name(), activeTurnState.getStateName());
    }

    @Test
    void testCanShootReturnsTrue() {
        assertTrue(activeTurnState.canShoot());
    }

    @Test
    void testCanPlaceShipReturnsFalse() {
        assertFalse(activeTurnState.canPlaceShip());
    }

    @Test
    void testOnEnterSetsPlayerTurnTrue() {
        activeTurnState.onEnter(mockActions);

        verify(mockActions).setPlayerTurn(true);
        verify(mockActions).refreshFleetUI();
    }

    @Test
    void testHandleOpponentGridClickSendsShotAndTransitions() {
        Coordinate coord = new Coordinate(0, 0);
        when(mockActions.getOpponentCellState(coord)).thenReturn(CellState.EMPTY);

        activeTurnState.handleOpponentGridClick(mockActions, coord);

        verify(mockActions).fireShot(coord);
        verify(mockActions).transitionToWaitingOpponent();
    }

    @Test
    void testHandleOpponentGridClickWhenAlreadyShotBlocksShot() {
        Coordinate coord = new Coordinate(1, 1);
        when(mockActions.getOpponentCellState(coord)).thenReturn(CellState.HIT);

        activeTurnState.handleOpponentGridClick(mockActions, coord);

        // Should not send shot
        verify(mockActions, never()).fireShot(any());
        // Should not transition
        verify(mockActions, never()).transitionToWaitingOpponent();
        // Show error message
        verify(mockActions).notifyUser(contains("already shot"));
    }

    @Test
    void testHandleOpponentGridHoverShowsShotPreview() {
        Coordinate coord = new Coordinate(3, 5);

        activeTurnState.handleOpponentGridHover(mockActions, coord);

        verify(mockActions).showShotPreview(coord);
    }
}
