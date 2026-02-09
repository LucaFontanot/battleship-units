package battleship.controller.turn.states;

import battleship.controller.mode.GameModeStrategy;
import battleship.controller.turn.TurnManager;
import battleship.model.game.FleetManager;
import battleship.model.game.Grid;
import battleship.model.game.Ship;
import battleship.view.BattleshipView;
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

class TestWaitingOpponentState {

    @Mock
    private TurnManager mockTurnManager;
    @Mock
    private BattleshipView mockView;
    @Mock
    private FleetManager mockFleetManager;
    @Mock
    private Grid mockGrid;
    @Mock
    private GameModeStrategy mockGameModeStrategy;

    private WaitingOpponentState waitingOpponentState;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        waitingOpponentState = new WaitingOpponentState();

        when(mockTurnManager.getView()).thenReturn(mockView);
        when(mockTurnManager.getFleetManager()).thenReturn(mockFleetManager);
        when(mockTurnManager.getGameModeStrategy()).thenReturn(mockGameModeStrategy);
        when(mockFleetManager.getGrid()).thenReturn(mockGrid);
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

        verify(mockView).setPlayerTurn(false);
    }

    @Test
    void testHandleOpponentGridUpdateCallsSuperMethod() {
        String gridSerialized = "grid_data";
        List<Ship> fleet = List.of();

        waitingOpponentState.handleOpponentGridUpdate(mockTurnManager, gridSerialized, fleet);

        verify(mockView).updateOpponentGrid(gridSerialized, fleet);
    }

    @Test
    void testHandleIncomingShotWithHitTransitionsToActiveTurn() {
        Coordinate coord = new Coordinate(0, 0);
        when(mockFleetManager.handleIncomingShot(coord)).thenReturn(true);
        when(mockFleetManager.isGameOver()).thenReturn(false);
        when(mockFleetManager.getFleet()).thenReturn(List.of());

        waitingOpponentState.handleIncomingShot(mockTurnManager, coord);

        verify(mockFleetManager).handleIncomingShot(coord);
        verify(mockGameModeStrategy).sendGridUpdate(any(Grid.class), any(List.class), eq(true));
        verify(mockView).updatePlayerGrid(any(String.class), any(List.class));
        verify(mockTurnManager).transitionTo(any(ActiveTurnState.class));
    }

    @Test
    void testHandleIncomingShotWithMissTransitionsToActiveTurn() {
        Coordinate coord = new Coordinate(0, 0);
        when(mockFleetManager.handleIncomingShot(coord)).thenReturn(false);
        when(mockFleetManager.isGameOver()).thenReturn(false);
        when(mockFleetManager.getFleet()).thenReturn(List.of());

        waitingOpponentState.handleIncomingShot(mockTurnManager, coord);

        verify(mockFleetManager).handleIncomingShot(coord);
        verify(mockGameModeStrategy).sendGridUpdate(any(Grid.class), any(List.class), eq(false));
        verify(mockView).updatePlayerGrid(any(String.class), any(List.class));
        verify(mockTurnManager).transitionTo(any(ActiveTurnState.class));
    }

    @Test
    void testHandleIncomingShotWithGameOverTransitionsToGameOverState() {
        Coordinate coord = new Coordinate(0, 0);
        when(mockFleetManager.handleIncomingShot(coord)).thenReturn(true);
        when(mockFleetManager.isGameOver()).thenReturn(true);
        when(mockFleetManager.getFleet()).thenReturn(List.of());

        waitingOpponentState.handleIncomingShot(mockTurnManager, coord);

        verify(mockFleetManager).handleIncomingShot(coord);
        verify(mockGameModeStrategy).sendGridUpdate(any(Grid.class), any(List.class), eq(true));
        verify(mockView).updatePlayerGrid(any(String.class), any(List.class));
        verify(mockTurnManager).transitionTo(any(GameOverState.class));
    }

    @Test
    void testHandleIncomingShotUpdatesPlayerGrid() {
        Coordinate coord = new Coordinate(3, 5);
        when(mockFleetManager.handleIncomingShot(coord)).thenReturn(true);
        when(mockFleetManager.isGameOver()).thenReturn(false);
        when(mockFleetManager.getFleet()).thenReturn(List.of());

        waitingOpponentState.handleIncomingShot(mockTurnManager, coord);

        verify(mockView).updatePlayerGrid(any(String.class), any(List.class));
    }
}
