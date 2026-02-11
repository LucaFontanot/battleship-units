package battleship.controller.turn.states;

import it.units.battleship.controller.turn.TurnManager;
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
    private TurnManager mockTurnManager;

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
        activeTurnState.onEnter(mockTurnManager);

        verify(mockTurnManager).setPlayerTurn(true);
        verify(mockTurnManager).refreshUI();
    }

    @Test
    void testHandleOpponentGridClickSendsShotAndTransitions() {
        Coordinate coord = new Coordinate(0, 0);
        when(mockTurnManager.getOpponentCellState(coord)).thenReturn(CellState.EMPTY);

        activeTurnState.handleOpponentGridClick(mockTurnManager, coord);

        verify(mockTurnManager).executeShot(coord);
        verify(mockTurnManager).transitionToWaitingOpponent();
    }

    @Test
    void testHandleOpponentGridClickWhenAlreadyShotBlocksShot() {
        Coordinate coord = new Coordinate(1, 1);
        when(mockTurnManager.getOpponentCellState(coord)).thenReturn(CellState.HIT);

        activeTurnState.handleOpponentGridClick(mockTurnManager, coord);

        // Should not send shot
        verify(mockTurnManager, never()).executeShot(any());
        // Should not transition
        verify(mockTurnManager, never()).transitionToWaitingOpponent();
        // Show error message
        verify(mockTurnManager).notifyUser(contains("already shot"));
    }

    @Test
    void testHandleOpponentGridHoverShowsShotPreview() {
        Coordinate coord = new Coordinate(3, 5);

        activeTurnState.handleOpponentGridHover(mockTurnManager, coord);

        verify(mockTurnManager).renderShotPreview(coord);
    }
}
