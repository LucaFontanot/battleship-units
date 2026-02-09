package battleship.controller.turn.states;

import battleship.controller.turn.TurnManager;
import battleship.controller.turn.TurnState;
import battleship.model.game.FleetManager;
import battleship.model.game.Ship;
import battleship.view.BattleshipView;
import it.units.battleship.*;

import java.util.List;
import java.util.Map;

/**
 * Base abstract class for turn states.
 * Provides default implementations for TurnState methods and common utility logic.
 */
public abstract class BaseGameState implements TurnState {

    @Override
    public void onEnter(TurnManager manager) {
        Logger.debug("Entering state: " + getStateName());
        updateGridAndFleetUI(manager);
    }

    protected void updateGridAndFleetUI(TurnManager manager){
        FleetManager fleetManager = manager.getFleetManager();
        BattleshipView view = manager.getView();

        String gridSerialized = GridMapper.serialize(fleetManager.getGrid().getGrid());
        view.updatePlayerGrid(gridSerialized, fleetManager.getFleet());
        refreshFleetUI(manager);
    }

    protected void refreshFleetUI(TurnManager manager){
        FleetManager fleetManager = manager.getFleetManager();
        BattleshipView view = manager.getView();

        Map<ShipType, Integer> placedCounts = fleetManager.getPlacedCounts();
        Map<ShipType, Integer> requiredCounts = fleetManager.getRequiredFleetConfiguration();

        view.refreshFleetSelection(placedCounts, requiredCounts);
    }

    @Override
    public void onExit(TurnManager manager) {
        Logger.debug("Exiting state: " + getStateName());
    }

    @Override
    public void handleOpponentGridClick(TurnManager manager, Coordinate coordinate) {}

    @Override
    public void handlePlayerGridClick(TurnManager manager, Coordinate coordinate) {}

    @Override
    public void handleOpponentGridHover(TurnManager manager, Coordinate coordinate) {}

    @Override
    public void handlePlayerGridHover(TurnManager manager, Coordinate coordinate) {}

    @Override
    public void handleIncomingShot(TurnManager manager, Coordinate coordinate) {}

    @Override
    public void handleOpponentGridUpdate(TurnManager manager, String grid, List<Ship> fleet) {
        manager.getView().updateOpponentGrid(grid, fleet);
    }

    @Override
    public void handleGameStatusReceived(TurnManager manager, GameState state) {}

    @Override
    public boolean canShoot() {
        return false;
    }

    @Override
    public boolean canPlaceShip() {
        return false;
    }
}
