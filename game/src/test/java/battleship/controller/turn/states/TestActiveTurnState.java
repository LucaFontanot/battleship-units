package battleship.controller.turn.states;

import battleship.controller.mode.GameModeStrategy;
import battleship.controller.turn.TurnManager;
import battleship.view.GameView;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestActiveTurnState {

    @Mock
    private TurnManager mockTurnManager;
    @Mock
    private GameView mockView;
    @Mock
    private GameModeStrategy mockGameModeStrategy;

    private ActiveTurnState activeTurnState;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        activeTurnState = new ActiveTurnState();

        when(mockTurnManager.getView()).thenReturn(mockView);
        when(mockTurnManager.getGameModeStrategy()).thenReturn(mockGameModeStrategy);
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

        verify(mockView).setPlayerTurn(true);
    }

    @Test
    void testHandleOpponentGridClickSendsShotAndTransitions() {
        Coordinate coord = new Coordinate(0, 0);

        activeTurnState.handleOpponentGridClick(mockTurnManager, coord);

        verify(mockGameModeStrategy).sendShot(coord);
        verify(mockTurnManager).transitionTo(any(WaitingOpponentState.class));
    }

    @Test
    void testHandleOpponentGridHoverShowsShotPreview() {
        Coordinate coord = new Coordinate(3, 5);

        activeTurnState.handleOpponentGridHover(mockTurnManager, coord);

        verify(mockView).showShotPreview(coord);
    }

    @Test
    void testHandleOpponentGridHoverWithDifferentCoordinates() {
        Coordinate coord1 = new Coordinate(0, 0);
        Coordinate coord2 = new Coordinate(9, 9);

        activeTurnState.handleOpponentGridHover(mockTurnManager, coord1);
        activeTurnState.handleOpponentGridHover(mockTurnManager, coord2);

        verify(mockView).showShotPreview(coord1);
        verify(mockView).showShotPreview(coord2);
    }
}
