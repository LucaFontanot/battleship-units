package battleship.controller.turn.states;

import battleship.controller.turn.TurnManager;
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
    public void onEnter(TurnManager manager) {
        super.onEnter(manager);
        manager.setPlayerTurn(true);
    }

    @Override
    public void handleOpponentGridClick(TurnManager manager, Coordinate coordinate) {
        CellState currentCellState = manager.getOpponentCellState(coordinate);

        if (currentCellState != CellState.EMPTY) {
            manager.notifyUser("You already shot here!");
            return;
        }
        manager.executeShot(coordinate);
        manager.transitionToWaitingOpponent();
    }

    @Override
    public void handleOpponentGridHover(TurnManager manager, Coordinate coordinate) {
        manager.renderShotPreview(coordinate);
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
