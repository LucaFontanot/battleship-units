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
import it.units.battleship.controller.turn.states.WaitingSetupState;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestWaitingSetupState {

    // ===== Fake StateTransitions: records which transitions was called =====
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
    private Grid playerGrid;
    private Grid opponentGrid;
    private WaitingSetupState waitingSetupState;

    @BeforeEach
    void setUp() {
        fakeView = new FakeView();
        fakeGameMode = new FakeGameMode();
        fakeTransitions = new FakeTransitions();
        playerGrid = new Grid(10, 10);
        opponentGrid = new Grid(10, 10);
        fleetManager = new FleetManager(playerGrid, Map.of(ShipType.DESTROYER, 1));

        GameViewMediator viewActions = new GameViewMediator(fakeView, fleetManager, opponentGrid);
        NetworkAdapter networkActions = new NetworkAdapter(fakeGameMode);
        GameContext context = new GameContext(viewActions, fakeTransitions, networkActions, fleetManager, opponentGrid);
        waitingSetupState = new WaitingSetupState(context);
    }

    @Test
    void testGetStateName_returnsWaitingSetup() {
        assertEquals("WAITING_SETUP", waitingSetupState.getStateName());
    }

    @Test
    void testCanShoot_returnsFalse() {
        assertFalse(waitingSetupState.canShoot());
    }

    @Test
    void testCanPlaceShip_returnsFalse() {
        assertFalse(waitingSetupState.canPlaceShip());
    }

    @Test
    void testOnEnter_disablesInteractionsAndShowMessage() {
        waitingSetupState.onEnter();

        assertFalse(fakeView.playerTurn);
        assertEquals("Waiting for opponent setup...", fakeView.lastSystemMessage);
        assertNotNull(fakeView.lastPlacedCounts);
    }

    @Test
    void testHandleGameStatusReceived_activeTurn_transitionsToActiveState() {
        waitingSetupState.handleGameStatusReceived(GameState.ACTIVE_TURN, "");

        assertTrue(fakeView.transitionToGamePhaseCalled);
        assertTrue(fakeTransitions.transitionedToActiveTurn);
    }

    @Test
    void testHandleGameStatusReceived_waitingOpponent_transitionsToWaitingState() {
        waitingSetupState.handleGameStatusReceived(GameState.WAITING_FOR_OPPONENT, "");

        assertTrue(fakeView.transitionToGamePhaseCalled);
        assertTrue(fakeTransitions.transitionedToWaitingOpponent);
    }

    @Test
    void testHandleGameStatusReceived_gameOver_doesNotTransition() {
        waitingSetupState.handleGameStatusReceived(GameState.GAME_OVER, "");

        assertFalse(fakeTransitions.transitionedToActiveTurn);
        assertFalse(fakeTransitions.transitionedToWaitingOpponent);
    }

    @Test
    void testHandleGameStatusReceived_setup_doesNotTransition() {
        waitingSetupState.handleGameStatusReceived(GameState.SETUP, "");

        assertFalse(fakeTransitions.transitionedToActiveTurn);
        assertFalse(fakeTransitions.transitionedToWaitingOpponent);
    }

    @Test
    void testPlayerCannotInteract_withGridsInWaitingSetup() {
        Coordinate coord = new Coordinate(0, 0);
        waitingSetupState.handlePlayerGridClick(coord);
        waitingSetupState.handleOpponentGridClick(coord);
        waitingSetupState.handlePlayerGridHover(coord);
        waitingSetupState.handleOpponentGridHover(coord);

        // No transition should have been triggered
        assertFalse(fakeTransitions.transitionedToActiveTurn);
        assertFalse(fakeTransitions.transitionedToWaitingOpponent);
        assertFalse(fakeTransitions.transitionedToWaitingSetup);
    }
}
