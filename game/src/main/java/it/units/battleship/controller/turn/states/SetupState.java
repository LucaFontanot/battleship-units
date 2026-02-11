package it.units.battleship.controller.turn.states;

import it.units.battleship.controller.turn.GameActions;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;

/**
 * Represents the Setup phase where players can place their ships on the grid.
 */
public class SetupState extends BaseGameState {
    @Override
    public void onEnter(GameActions actions) {
        super.onEnter(actions);
        actions.refreshFleetUI();
        actions.setPlayerTurn(true);
    }

    @Override
    public void handlePlayerGridClick(GameActions actions, Coordinate coordinate) {
        actions.placeShip(coordinate);
    }

    @Override
    public void handlePlayerGridHover(GameActions actions, Coordinate coordinate) {
        actions.previewPlacement(coordinate);
    }

    @Override
    public String getStateName() {
        return GameState.SETUP.name();
    }

    @Override
    public boolean canPlaceShip() {
        return true;
    }
}
