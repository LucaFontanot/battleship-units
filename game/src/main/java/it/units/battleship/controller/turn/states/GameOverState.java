package it.units.battleship.controller.turn.states;

import it.units.battleship.GameState;
import it.units.battleship.controller.turn.GameContext;
import lombok.Getter;

/**
 * Represents the state of the game when it has ended, either in victory or defeat.
 */
@Getter
public class GameOverState extends BaseGameState {

    private final String message;
    private final boolean won;

    public GameOverState(GameContext context, boolean won, String message) {
        super(context);
        this.message = message;
        this.won = won;
    }

    @Override
    public void onEnter() {
        super.onEnter();
        view.setPlayerTurn(false);
        view.showEndGame(message);

        if (!won) {
            network.sendGameOver(message);
        }
    }

    @Override
    public String getStateName() {
        return GameState.GAME_OVER.name();
    }
}
