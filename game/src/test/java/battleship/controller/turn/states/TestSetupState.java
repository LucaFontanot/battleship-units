package battleship.controller.turn.states;

import battleship.testutil.FakeGameMode;
import battleship.testutil.FakeView;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import it.units.battleship.controller.turn.GameContext;
import it.units.battleship.controller.turn.adapters.GameViewMediator;
import it.units.battleship.controller.turn.adapters.NetworkAdapter;
import it.units.battleship.controller.turn.contracts.StateTransitions;
import it.units.battleship.controller.turn.states.SetupState;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestSetupState {

    private static class FakeTransitions implements StateTransitions {
        boolean transitionedToActiveTurn;
        boolean transitionedToWaitingOpponent;
        boolean transitionedToWaitingSetup;
        boolean transitionedToGameOver;

        @Override public void transitionToActiveTurn() { transitionedToActiveTurn = true; }
        @Override public void transitionToWaitingOpponent() { transitionedToWaitingOpponent = true; }
        @Override public void transitionToWaitingSetup() { transitionedToWaitingSetup = true; }
        @Override public void transitionToGameOver(boolean won, String message) { transitionedToGameOver = true; }
    }

    private FakeView fakeView;
    private FakeGameMode fakeGameMode;
    private FakeTransitions fakeTransitions;
    private FleetManager fleetManager;
    private SetupState setupState;

    @BeforeEach
    void setUp() {
        fakeView = new FakeView();
        fakeGameMode = new FakeGameMode();
        fakeTransitions = new FakeTransitions();
        Grid playerGrid = new Grid(10, 10);
        Grid opponentGrid = new Grid(10, 10);
        // Using a small fleet for testing
        fleetManager = new FleetManager(playerGrid, Map.of(ShipType.DESTROYER, 1));

        GameViewMediator viewActions = new GameViewMediator(fakeView, fleetManager, opponentGrid);
        NetworkAdapter networkActions = new NetworkAdapter(fakeGameMode);
        GameContext context = new GameContext(viewActions, fakeTransitions, networkActions, fleetManager, opponentGrid);
        setupState = new SetupState(context, viewActions);
    }

    @Test
    void testGetStateNameReturnsSetup() {
        assertEquals(GameState.SETUP.name(), setupState.getStateName());
    }

    @Test
    void testCanPlaceShipReturnsTrue() {
        assertTrue(setupState.canPlaceShip());
    }

    @Test
    void testCanShootReturnsFalse() {
        assertFalse(setupState.canShoot());
    }

    @Test
    void testOnEnterSetsPlayerTurnTrue() {
        setupState.onEnter();

        assertTrue(fakeView.playerTurn);
        assertNotNull(fakeView.lastPlacedCounts);
    }

    @Test
    void testHandlePlayerGridClickWithNullShipTypeDoesNothing() {
        Coordinate coord = new Coordinate(0, 0);
        fakeView.selectedShipType = null;
        fakeView.selectedOrientation = Orientation.HORIZONTAL_RIGHT;

        setupState.handlePlayerGridClick(coord);

        assertTrue(fleetManager.getFleet().isEmpty());
    }

    @Test
    void testHandlePlayerGridClickPlacesShipSuccessfully() {
        Coordinate coord = new Coordinate(0, 0);
        // Make sure we have a larger fleet config so it doesn't complete immediately
        Grid playerGrid = new Grid(10, 10);
        fleetManager = new FleetManager(playerGrid, Map.of(ShipType.DESTROYER, 2));
        setUpStateWithFleetManager(fleetManager);

        fakeView.selectedShipType = ShipType.DESTROYER;
        fakeView.selectedOrientation = Orientation.HORIZONTAL_RIGHT;

        setupState.handlePlayerGridClick(coord);

        assertEquals(1, fleetManager.getFleet().size());
        assertNotNull(fakeView.lastPlayerGridSerialized);
        assertNotNull(fakeView.lastPlacedCounts);
    }

    private void setUpStateWithFleetManager(FleetManager fm) {
        Grid opponentGrid = new Grid(10, 10);
        GameViewMediator viewActions = new GameViewMediator(fakeView, fm, opponentGrid);
        NetworkAdapter networkActions = new NetworkAdapter(fakeGameMode);
        GameContext context = new GameContext(viewActions, fakeTransitions, networkActions, fm, opponentGrid);
        setupState = new SetupState(context, viewActions);
    }

    @Test
    void testHandlePlayerGridClickCompletesFleetAndTransitions() {
        Coordinate coord = new Coordinate(0, 0);
        fakeView.selectedShipType = ShipType.DESTROYER;
        fakeView.selectedOrientation = Orientation.HORIZONTAL_RIGHT;

        setupState.handlePlayerGridClick(coord);

        assertTrue(fakeTransitions.transitionedToWaitingSetup);
        assertTrue(fakeGameMode.setupCompleteNotified);
    }

    @Test
    void testHandlePlayerGridHoverShowsPreview() {
        Coordinate coord = new Coordinate(0, 0);
        fakeView.selectedShipType = ShipType.DESTROYER;
        fakeView.selectedOrientation = Orientation.HORIZONTAL_RIGHT;

        setupState.handlePlayerGridHover(coord);

        assertNotNull(fakeView.lastPlacementCoords);
        assertTrue(fakeView.lastPlacementValid);
        assertNotNull(fakeView.lastPreviewShip);
    }

    @Test
    void testHandlePlayerGridHoverWithNullShipTypeDoesNothing() {
        Coordinate coord = new Coordinate(0, 0);
        fakeView.selectedShipType = null;

        setupState.handlePlayerGridHover(coord);

        assertNull(fakeView.lastPlacementCoords);
    }
}
