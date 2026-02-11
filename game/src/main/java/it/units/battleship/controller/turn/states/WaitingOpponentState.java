package it.units.battleship.controller.turn.states;

import it.units.battleship.controller.turn.TurnManager;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;

/**
 * Represents the state where the player is waiting for the opponent's next move.
 * The state handles opponent grid updates, incoming shots.
 */
public class WaitingOpponentState extends BaseGameState {
    @Override
    public void onEnter(TurnManager manager) {
        super.onEnter(manager);
        manager.setPlayerTurn(false);
    }

    @Override
    public void handleIncomingShot(TurnManager manager, Coordinate coordinate) {
        boolean gameOver = manager.processIncomingShot(coordinate);

        if (gameOver) {
            manager.transitionToGameOver(false, "You lost! All your ships are sunk.");
        } else {
            manager.transitionToActiveTurn();
        }
    }

    @Override
    public String getStateName() {
        return GameState.WAITING_FOR_OPPONENT.name();
    }
}
