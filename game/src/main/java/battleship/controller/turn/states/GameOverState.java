package battleship.controller.turn.states;

import battleship.controller.turn.TurnManager;
import battleship.controller.turn.TurnState;
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
    public void onEnter(TurnManager manager) {
        super.onEnter(manager);
        manager.getView().setPlayerTurn(false);
        manager.getView().showEndGamePhase(message);

        if (!won) {
            manager.getGameModeStrategy().sendGameOver(message);
        }
    }

    @Override
    public String getStateName() {
        return GameState.GAME_OVER.name();
    }
}
