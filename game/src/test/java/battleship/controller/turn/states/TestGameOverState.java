package battleship.controller.turn.states;

import battleship.controller.mode.GameModeStrategy;
import battleship.controller.turn.TurnManager;
import battleship.view.GameView;
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
    @Mock
    private GameView mockView;
    @Mock
    private GameModeStrategy mockGameModeStrategy;

    private GameOverState gameOverState;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockTurnManager.getView()).thenReturn(mockView);
        when(mockTurnManager.getGameModeStrategy()).thenReturn(mockGameModeStrategy);
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

        verify(mockView).setPlayerTurn(false);
        verify(mockView).showEndGamePhase(message);
        verify(mockGameModeStrategy, never()).sendGameOver(anyString());
    }

    @Test
    void testOnEnterWithLossSendsGameOver() {
        String message = "You lost!";
        gameOverState = new GameOverState(false, message);

        gameOverState.onEnter(mockTurnManager);

        verify(mockView).setPlayerTurn(false);
        verify(mockView).showEndGamePhase(message);
        verify(mockGameModeStrategy).sendGameOver(message);
    }

    @Test
    void testOnEnterSetsPlayerTurnFalse() {
        gameOverState = new GameOverState(true, "Game Over");

        gameOverState.onEnter(mockTurnManager);

        verify(mockView).setPlayerTurn(false);
    }

    @Test
    void testOnEnterShowsEndGamePhase() {
        String message = "Game Over Message";
        gameOverState = new GameOverState(true, message);

        gameOverState.onEnter(mockTurnManager);

        verify(mockView).showEndGamePhase(message);
    }
}
