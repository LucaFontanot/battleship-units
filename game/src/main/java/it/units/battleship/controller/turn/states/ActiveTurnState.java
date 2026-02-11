package it.units.battleship.controller.turn.states;

import it.units.battleship.CellState;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.controller.turn.GameContext;

/**
 * Represents the state where a player's turn is active in the game.
 * This state allows the player to interact with the game board by firing shots
 * and previewing potential shots on the opponent's grid.
 */
public class ActiveTurnState extends BaseGameState {

    public ActiveTurnState(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        view.setPlayerTurn(true);
    }

    @Override
    public void handleOpponentGridClick(Coordinate coordinate) {
        CellState currentCellState = opponentGrid.getState(coordinate);

        if (currentCellState != CellState.EMPTY) {
            view.notifyUser("You already shot here!");
            return;
        }
        network.fireShot(coordinate);
        stateTransitions.transitionToWaitingOpponent();
    }

    @Override
    public void handleOpponentGridHover(Coordinate coordinate) {
        view.showShotPreview(coordinate);
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
