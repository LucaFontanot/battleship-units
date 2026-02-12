package it.units.battleship.controller.turn.states;

import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.controller.turn.GameContext;

import static it.units.battleship.Defaults.MSG_DEFEAT;

/**
 * Represents the state where the player is waiting for the opponent's next move.
 * The state handles opponent grid updates, incoming shots.
 */
public class WaitingOpponentState extends BaseGameState {

    public WaitingOpponentState(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        view.setPlayerTurn(false);
    }

    @Override
    public void handleIncomingShot(Coordinate coordinate) {
        boolean gameOver = processIncomingShot(coordinate);

        if (gameOver) {
            stateTransitions.transitionToGameOver(false, MSG_DEFEAT);
        } else {
            stateTransitions.transitionToActiveTurn();
        }
    }

    private boolean processIncomingShot(Coordinate coordinate) {
        boolean hit = fleetManager.handleIncomingShot(coordinate);

        network.sendGridUpdate(
                fleetManager.getGrid(),
                fleetManager.getFleet(),
                hit
        );

        view.refreshPlayerGrid();
        return fleetManager.isGameOver();
    }

    @Override
    public String getStateName() {
        return GameState.WAITING_FOR_OPPONENT.name();
    }
}
