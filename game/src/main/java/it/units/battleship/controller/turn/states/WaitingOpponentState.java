package it.units.battleship.controller.turn.states;

import it.units.battleship.controller.turn.GameActions;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;

/**
 * Represents the state where the player is waiting for the opponent's next move.
 * The state handles opponent grid updates, incoming shots.
 */
public class WaitingOpponentState extends BaseGameState {
    @Override
    public void onEnter(GameActions actions) {
        super.onEnter(actions);
        actions.setPlayerTurn(false);
    }

    @Override
    public void handleIncomingShot(GameActions actions, Coordinate coordinate) {
        boolean gameOver = actions.processIncomingShot(coordinate);

        if (gameOver) {
            actions.transitionToGameOver(false, "You lost! All your ships are sunk.");
        } else {
            actions.transitionToActiveTurn();
        }
    }

    @Override
    public String getStateName() {
        return GameState.WAITING_FOR_OPPONENT.name();
    }
}
