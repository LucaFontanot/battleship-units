package it.units.battleship.controller.turn.states;

import it.units.battleship.controller.turn.GameActions;
import it.units.battleship.CellState;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;

/**
 * Represents the state where a player's turn is active in the game.
 * This state allows the player to interact with the game board by firing shots
 * and previewing potential shots on the opponent's grid.
 */
public class ActiveTurnState extends BaseGameState {

    @Override
    public void onEnter(GameActions actions) {
        super.onEnter(actions);
        actions.setPlayerTurn(true);
    }

    @Override
    public void handleOpponentGridClick(GameActions actions, Coordinate coordinate) {
        CellState currentCellState = actions.getOpponentCellState(coordinate);

        if (currentCellState != CellState.EMPTY) {
            actions.notifyUser("You already shot here!");
            return;
        }
        actions.fireShot(coordinate);
        actions.transitionToWaitingOpponent();
    }

    @Override
    public void handleOpponentGridHover(GameActions actions, Coordinate coordinate) {
        actions.showShotPreview(coordinate);
    }

    @Override
    public String getStateName() {
        return GameState.ACTIVE_TURN.name();
    }

    @Override
    public boolean canShoot() {
        return true;
    }
}
