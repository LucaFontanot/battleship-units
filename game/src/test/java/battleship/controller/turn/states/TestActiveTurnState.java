package battleship.controller.turn.states;

import battleship.testutil.FakeGameMode;
import battleship.testutil.FakeView;
import it.units.battleship.CellState;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.ShipType;
import it.units.battleship.controller.turn.GameContext;
import it.units.battleship.controller.turn.adapters.GameViewMediator;
import it.units.battleship.controller.turn.adapters.NetworkAdapter;
import it.units.battleship.controller.turn.contracts.StateTransitions;
import it.units.battleship.controller.turn.states.ActiveTurnState;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestActiveTurnState {

    private static class FakeTransitions implements StateTransitions {
        boolean transitionedToActiveTurn;
        boolean transitionedToWaitingOpponent;
        boolean transitionedToWaitingSetup;
        boolean transitionedToGameOver;

        @Override
        public void transitionToActiveTurn() { transitionedToActiveTurn = true; }
        @Override
        public void transitionToWaitingOpponent() { transitionedToWaitingOpponent = true; }
        @Override
        public void transitionToWaitingSetup() { transitionedToWaitingSetup = true; }
        @Override
        public void transitionToGameOver(boolean won, String message) { transitionedToGameOver = true; }
    }

    private FakeView fakeView;
    private FakeGameMode fakeGameMode;
    private FakeTransitions fakeTransitions;
    private FleetManager fleetManager;
    private Grid opponentGrid;
    private ActiveTurnState activeTurnState;

    @BeforeEach
    void setUp() {
        fakeView = new FakeView();
        fakeGameMode = new FakeGameMode();
        fakeTransitions = new FakeTransitions();
        Grid playerGrid = new Grid(10, 10);
        opponentGrid = new Grid(10, 10);
        fleetManager = new FleetManager(playerGrid, Map.of(ShipType.DESTROYER, 1));

        GameViewMediator viewActions = new GameViewMediator(fakeView, fleetManager, opponentGrid);
        NetworkAdapter networkActions = new NetworkAdapter(fakeGameMode);
        GameContext context = new GameContext(viewActions, fakeTransitions, networkActions, fleetManager, opponentGrid);
        activeTurnState = new ActiveTurnState(context);
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
        activeTurnState.onEnter();

        assertTrue(fakeView.playerTurn);
        assertNotNull(fakeView.lastPlacedCounts);
    }

    @Test
    void testHandleOpponentGridClickSendsShotAndTransitions() {
        Coordinate coord = new Coordinate(0, 0);
        // opponentGrid is empty by default

        activeTurnState.handleOpponentGridClick(coord);

        assertEquals(coord, fakeGameMode.lastShotSent);
        assertTrue(fakeTransitions.transitionedToWaitingOpponent);
    }

    @Test
    void testHandleOpponentGridClickWhenAlreadyShotBlocksShot() {
        Coordinate coord = new Coordinate(1, 1);
        opponentGrid.changeState(coord, CellState.HIT);

        activeTurnState.handleOpponentGridClick(coord);

        assertNull(fakeGameMode.lastShotSent);
        assertFalse(fakeTransitions.transitionedToWaitingOpponent);
        assertTrue(fakeView.lastSystemMessage.contains("already shot"));
    }

    @Test
    void testHandleOpponentGridHoverShowsShotPreview() {
        Coordinate coord = new Coordinate(3, 5);

        activeTurnState.handleOpponentGridHover(coord);

        assertEquals(coord, fakeView.lastShotPreview);
    }
}
