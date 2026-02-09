package battleship.controller.turn.states;

import battleship.controller.mode.GameModeStrategy;
import battleship.controller.turn.TurnManager;
import battleship.model.FleetManager;
import battleship.model.Grid;
import battleship.view.core.BattleshipView;
import it.units.battleship.CellState;
import it.units.battleship.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestGameOverState {

    @Mock
    private TurnManager mockTurnManager;

    private GameOverState gameOverState;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetStateNameReturnsGameOver() {
        gameOverState = new GameOverState(true, "You won!");
        assertEquals(GameState.GAME_OVER.name(), gameOverState.getStateName());
    }

    @Test
    void testCanShootReturnsFalse() {
        gameOverState = new GameOverState(true, "You won!");
        assertFalse(gameOverState.canShoot());
    }

    @Test
    void testCanPlaceShipReturnsFalse() {
        gameOverState = new GameOverState(true, "You won!");
        assertFalse(gameOverState.canPlaceShip());
    }

    @Test
    void testConstructorWithWin() {
        String message = "You won!";
        gameOverState = new GameOverState(true, message);

        assertTrue(gameOverState.isWon());
        assertEquals(message, gameOverState.getMessage());
    }

    @Test
    void testConstructorWithLoss() {
        String message = "You lost!";
        gameOverState = new GameOverState(false, message);

        assertFalse(gameOverState.isWon());
        assertEquals(message, gameOverState.getMessage());
    }

    @Test
    void testOnEnterWithWinDoesNotSendGameOver() {
        String message = "You won!";
        gameOverState = new GameOverState(true, message);

        gameOverState.onEnter(mockTurnManager);

        verify(mockTurnManager).setPlayerTurn(false);
        verify(mockTurnManager).transitionToEndGamePhase(message);
        verify(mockTurnManager, never()).sendGameOverStatus(anyString());
        verify(mockTurnManager).refreshUI();
    }

    @Test
    void testOnEnterWithLossSendsGameOver() {
        String message = "You lost!";
        gameOverState = new GameOverState(false, message);

        gameOverState.onEnter(mockTurnManager);

        verify(mockTurnManager).setPlayerTurn(false);
        verify(mockTurnManager).transitionToEndGamePhase(message);
        verify(mockTurnManager).sendGameOverStatus(message);
        verify(mockTurnManager).refreshUI();
    }
}
