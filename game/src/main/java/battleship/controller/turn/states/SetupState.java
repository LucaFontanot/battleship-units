package battleship.controller.turn.states;

import battleship.controller.turn.TurnManager;
import it.units.battleship.*;

/**
 * Represents the Setup phase where players can place their ships on the grid.
 */
public class SetupState extends BaseGameState {
    @Override
    public void onEnter(TurnManager manager) {
        super.onEnter(manager);
        manager.refreshFleetUI();
        manager.setPlayerTurn(true);
    }

    @Override
    public void handlePlayerGridClick(TurnManager manager, Coordinate coordinate) {
        manager.tryPlaceShip(coordinate);
    }

    @Override
    public void handlePlayerGridHover(TurnManager manager, Coordinate coordinate) {
        manager.previewPlacement(coordinate);
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
