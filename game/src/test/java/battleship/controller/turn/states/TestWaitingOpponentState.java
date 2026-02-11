package battleship.controller.turn.states;

import it.units.battleship.controller.turn.TurnManager;
import it.units.battleship.controller.turn.states.WaitingOpponentState;
import it.units.battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestWaitingOpponentState {

    @Mock
    private TurnManager mockTurnManager;

    private WaitingOpponentState waitingOpponentState;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        waitingOpponentState = new WaitingOpponentState();
    }

    @Test
    void testGetStateNameReturnsWaitingForOpponent() {
        assertEquals(GameState.WAITING_FOR_OPPONENT.name(), waitingOpponentState.getStateName());
    }

    @Test
    void testCanShootReturnsFalse() {
        assertFalse(waitingOpponentState.canShoot());
    }

    @Test
    void testCanPlaceShipReturnsFalse() {
        assertFalse(waitingOpponentState.canPlaceShip());
    }

    @Test
    void testOnEnterSetsPlayerTurnFalse() {
        waitingOpponentState.onEnter(mockTurnManager);

        verify(mockTurnManager).setPlayerTurn(false);
        verify(mockTurnManager).refreshUI();
    }

    @Test
    void testHandleOpponentGridUpdateCallsManagerMethod() {
        String gridSerialized = "grid_data";
        List<Ship> fleet = List.of();

        waitingOpponentState.handleOpponentGridUpdate(mockTurnManager, gridSerialized, fleet);

        verify(mockTurnManager).updateOpponentGrid(gridSerialized, fleet);
    }

    @Test
    void testHandleIncomingShotWithGameOverTransitionsToGameOverState() {
        Coordinate coord = new Coordinate(0, 0);
        when(mockTurnManager.processIncomingShot(coord)).thenReturn(true);

        waitingOpponentState.handleIncomingShot(mockTurnManager, coord);

        verify(mockTurnManager).processIncomingShot(coord);
        verify(mockTurnManager).transitionToGameOver(eq(false), anyString());
    }

    @Test
    void testHandleIncomingShotWithoutGameOverTransitionsToActiveTurn() {
        Coordinate coord = new Coordinate(0, 0);
        when(mockTurnManager.processIncomingShot(coord)).thenReturn(false);

        waitingOpponentState.handleIncomingShot(mockTurnManager, coord);

        verify(mockTurnManager).processIncomingShot(coord);
        verify(mockTurnManager).transitionToActiveTurn();
    }
}
