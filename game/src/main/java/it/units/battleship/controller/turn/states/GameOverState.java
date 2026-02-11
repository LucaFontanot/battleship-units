package it.units.battleship.controller.turn.states;

import it.units.battleship.controller.turn.GameActions;
import it.units.battleship.GameState;
import lombok.Getter;

/**
 * Represents the state of the game when it has ended, either in victory or defeat.
 */
@Getter
public class GameOverState extends BaseGameState {

    private final String message;
    private final boolean won;

    public GameOverState(boolean won, String message) {
        this.message = message;
        this.won = won;
    }

    @Override
    public void onEnter(GameActions actions) {
        super.onEnter(actions);
        actions.setPlayerTurn(false);
        actions.showEndGame(message);

        if (!won) {
            actions.sendGameOver(message);
        }
    }

    @Override
    public String getStateName() {
        return GameState.GAME_OVER.name();
    }
}
