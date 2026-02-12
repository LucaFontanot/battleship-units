package battleship.controller.turn.states;

import battleship.testutil.FakeGameMode;
import battleship.testutil.FakeView;
import it.units.battleship.GameState;
import it.units.battleship.ShipType;
import it.units.battleship.controller.turn.GameContext;
import it.units.battleship.controller.turn.adapters.GameViewMediator;
import it.units.battleship.controller.turn.adapters.NetworkAdapter;
import it.units.battleship.controller.turn.contracts.StateTransitions;
import it.units.battleship.controller.turn.states.GameOverState;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestGameOverState {

    private static class FakeTransitions implements StateTransitions {
        @Override public void transitionToActiveTurn() {}
        @Override public void transitionToWaitingOpponent() {}
        @Override public void transitionToWaitingSetup() {}
        @Override public void transitionToGameOver(boolean won, String message) {}
    }

    private FakeView fakeView;
    private FakeGameMode fakeGameMode;
    private GameContext context;
    private GameOverState gameOverState;

    @BeforeEach
    void setUp() {
        fakeView = new FakeView();
        fakeGameMode = new FakeGameMode();
        Grid playerGrid = new Grid(10, 10);
        Grid opponentGrid = new Grid(10, 10);
        FleetManager fleetManager = new FleetManager(playerGrid, Map.of(ShipType.DESTROYER, 1));

        GameViewMediator viewActions = new GameViewMediator(fakeView);
        NetworkAdapter networkActions = new NetworkAdapter(fakeGameMode);
        context = new GameContext(viewActions, new FakeTransitions(), networkActions, fleetManager, opponentGrid);
    }

    @Test
    void testGetStateNameReturnsGameOver() {
        gameOverState = new GameOverState(context, true, "You won!");
        assertEquals(GameState.GAME_OVER.name(), gameOverState.getStateName());
    }

    @Test
    void testCanShootReturnsFalse() {
        gameOverState = new GameOverState(context, true, "You won!");
        assertFalse(gameOverState.canShoot());
    }

    @Test
    void testCanPlaceShipReturnsFalse() {
        gameOverState = new GameOverState(context, true, "You won!");
        assertFalse(gameOverState.canPlaceShip());
    }

    @Test
    void testConstructorWithWin() {
        String message = "You won!";
        gameOverState = new GameOverState(context, true, message);

        assertTrue(gameOverState.isWon());
        assertEquals(message, gameOverState.getMessage());
    }

    @Test
    void testConstructorWithLoss() {
        String message = "You lost!";
        gameOverState = new GameOverState(context, false, message);

        assertFalse(gameOverState.isWon());
        assertEquals(message, gameOverState.getMessage());
    }

    @Test
    void testOnEnterWithWinDoesNotSendGameOver() {
        String message = "You won!";
        gameOverState = new GameOverState(context, true, message);

        gameOverState.onEnter();

        assertFalse(fakeView.playerTurn);
        assertEquals(message, fakeView.lastEndGameMessage);
        assertNull(fakeGameMode.lastGameOverMessage);
        assertNotNull(fakeView.lastPlacedCounts);
    }

    @Test
    void testOnEnterWithLossSendsGameOver() {
        String message = "You lost!";
        gameOverState = new GameOverState(context, false, message);

        gameOverState.onEnter();

        assertFalse(fakeView.playerTurn);
        assertEquals(message, fakeView.lastEndGameMessage);
        assertEquals(message, fakeGameMode.lastGameOverMessage);
        assertNotNull(fakeView.lastPlacedCounts);
    }
}
