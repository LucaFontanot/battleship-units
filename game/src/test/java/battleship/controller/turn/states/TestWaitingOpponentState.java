package battleship.controller.turn.states;

import battleship.testutil.FakeGameMode;
import battleship.testutil.FakeView;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.ShipType;
import it.units.battleship.controller.turn.GameContext;
import it.units.battleship.controller.turn.adapters.GameViewMediator;
import it.units.battleship.controller.turn.adapters.NetworkAdapter;
import it.units.battleship.controller.turn.contracts.StateTransitions;
import it.units.battleship.controller.turn.states.WaitingOpponentState;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestWaitingOpponentState {

    private static class FakeTransitions implements StateTransitions {
        boolean transitionedToActiveTurn;
        boolean transitionedToWaitingOpponent;
        boolean transitionedToWaitingSetup;
        boolean transitionedToGameOver;
        boolean lastWon;

        @Override public void transitionToActiveTurn() { transitionedToActiveTurn = true; }
        @Override public void transitionToWaitingOpponent() { transitionedToWaitingOpponent = true; }
        @Override public void transitionToWaitingSetup() { transitionedToWaitingSetup = true; }
        @Override public void transitionToGameOver(boolean won, String message) {
            transitionedToGameOver = true;
            lastWon = won;
        }
    }

    private FakeView fakeView;
    private FakeGameMode fakeGameMode;
    private FakeTransitions fakeTransitions;
    private FleetManager fleetManager;
    private Grid opponentGrid;
    private WaitingOpponentState waitingOpponentState;

    @BeforeEach
    void setUp() {
        fakeView = new FakeView();
        fakeGameMode = new FakeGameMode();
        fakeTransitions = new FakeTransitions();
        Grid playerGrid = new Grid(10, 10);
        opponentGrid = new Grid(10, 10);
        fleetManager = new FleetManager(playerGrid, Map.of(ShipType.DESTROYER, 1));

        GameViewMediator viewActions = new GameViewMediator(fakeView);
        NetworkAdapter networkActions = new NetworkAdapter(fakeGameMode);
        GameContext context = new GameContext(viewActions, fakeTransitions, networkActions, fleetManager, opponentGrid);
        waitingOpponentState = new WaitingOpponentState(context);
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
        waitingOpponentState.onEnter();

        assertFalse(fakeView.playerTurn);
        assertNotNull(fakeView.lastPlacedCounts);
    }

    @Test
    void testHandleOpponentGridUpdateCallsActions() {
        String gridSerialized = "grid_data";
        List<Ship> fleet = List.of();

        waitingOpponentState.handleOpponentGridUpdate(gridSerialized, fleet);

        assertEquals(gridSerialized, fakeView.lastOpponentGridSerialized);
        assertEquals(fleet, fakeView.lastOpponentFleet);
    }

    @Test
    void testHandleIncomingShotWithGameOverTransitionsToGameOverState() {
        Coordinate coord1 = new Coordinate(0, 0);
        Coordinate coord2 = new Coordinate(0, 1);
        // Place a ship (size 2)
        fleetManager.addShip(Ship.createShip(coord1, it.units.battleship.Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, fleetManager.getGrid()));
        
        // Hit the first part of the ship
        fleetManager.handleIncomingShot(coord1);

        // Call handleIncomingShot for the second part (sinking the ship and ending the game)
        waitingOpponentState.handleIncomingShot(coord2);

        assertTrue(fakeTransitions.transitionedToGameOver);
        assertFalse(fakeTransitions.lastWon);
    }

    @Test
    void testHandleIncomingShotWithoutGameOverTransitionsToActiveTurn() {
        Coordinate shipCoord = new Coordinate(5, 5);
        Coordinate shotCoord = new Coordinate(0, 0);
        // Place a ship so the fleet is NOT empty (empty fleet means game over in FleetManager)
        fleetManager.addShip(Ship.createShip(shipCoord, it.units.battleship.Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, fleetManager.getGrid()));

        waitingOpponentState.handleIncomingShot(shotCoord);

        assertTrue(fakeTransitions.transitionedToActiveTurn);
    }
}
