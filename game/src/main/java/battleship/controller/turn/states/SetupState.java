package battleship.controller.turn.states;

import battleship.controller.turn.TurnManager;
import battleship.model.game.FleetManager;
import battleship.model.game.Ship;
import battleship.view.BattleshipView;
import it.units.battleship.*;

import java.util.LinkedHashSet;

/**
 * Represents the Setup phase where players can place their ships on the grid.
 */
public class SetupState extends BaseGameState {
    @Override
    public void onEnter(TurnManager manager) {
        super.onEnter(manager);
        manager.getView().setPlayerTurn(true);
    }

    @Override
    public void handlePlayerGridClick(TurnManager manager, Coordinate coordinate) {
        BattleshipView view = manager.getView();
        FleetManager fleetManager = manager.getFleetManager();

        Orientation orientation = view.getSelectedOrientation();
        ShipType shipType = view.getSelectedShipType();

        if (shipType == null) return;

        try {
            Ship ship = Ship.createShip(coordinate, orientation, shipType, fleetManager.getGrid());
            boolean placed = fleetManager.addShip(ship);

            if (placed) {
                updateGridAndFleetUI(manager);

                if (fleetManager.isFleetComplete()) {
                    manager.onSetupComplete();
                }
            } else {
                view.playerErrorSound();
                view.showPlacementPreview(ship.getCoordinates(), false, ship);
            }
        } catch (IllegalArgumentException ex) {
            view.playerErrorSound();
            LinkedHashSet<Coordinate> coords = shipType.getShipCoordinates(coordinate, orientation);
            view.showPlacementPreview(coords, false, null);
        }
    }

    @Override
    public void handlePlayerGridHover(TurnManager manager, Coordinate coordinate) {
        BattleshipView view = manager.getView();
        FleetManager fleetManager = manager.getFleetManager();

        Orientation orientation = view.getSelectedOrientation();
        ShipType shipType = view.getSelectedShipType();

        if (shipType == null) return;

        try {
            Ship ship = Ship.createShip(coordinate, orientation, shipType, fleetManager.getGrid());
            boolean valid = fleetManager.canPlaceShip(ship);
            view.showPlacementPreview(ship.getCoordinates(), valid, ship);
        } catch (IllegalArgumentException ex) {
            LinkedHashSet<Coordinate> coords = shipType.getShipCoordinates(coordinate, orientation);
            view.showPlacementPreview(coords, false, null);
        }
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
