package battleship.controller.turn.states;

import battleship.controller.turn.TurnManager;
import battleship.controller.turn.TurnState;
import it.units.battleship.GameState;
import lombok.Getter;

@Getter
public class GameOverState implements TurnState {

    private final String message;
    private final boolean won;

    public GameOverState(boolean won, String message) {
        this.message = message;
        this.won = won;
    }

    @Override
    public void onEnter(TurnManager manager) {
        TurnState.super.onEnter(manager);
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
