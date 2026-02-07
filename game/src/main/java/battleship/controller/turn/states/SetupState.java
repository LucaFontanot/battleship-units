package battleship.controller.turn.states;

import battleship.controller.turn.TurnManager;
import battleship.controller.turn.TurnState;
import battleship.model.FleetManager;
import battleship.model.Ship;
import battleship.view.GameView;
import it.units.battleship.*;

import java.util.LinkedHashSet;
import java.util.Map;

public class SetupState implements TurnState {
    @Override
    public void onEnter(TurnManager manager) {
        TurnState.super.onEnter(manager);
        manager.getView().setPlayerTurn(true);
        refreshFleetUI(manager);
    }

    @Override
    public void handlePlayerGridClick(TurnManager manager, Coordinate coordinate) {
        GameView view = manager.getView();
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
        GameView view = manager.getView();
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

    private void updateGridAndFleetUI(TurnManager manager){
        FleetManager fleetManager = manager.getFleetManager();
        GameView view = manager.getView();

        String gridSerialized = GridMapper.serialize(fleetManager.getGrid().getGrid());
        view.updatePlayerGrid(gridSerialized, fleetManager.getFleet());
        refreshFleetUI(manager);
    }

    private void refreshFleetUI(TurnManager manager){
        FleetManager fleetManager = manager.getFleetManager();
        GameView view = manager.getView();

        Map<ShipType, Integer> placedCounts = fleetManager.getPlacedCounts();
        Map<ShipType, Integer> requiredCounts = fleetManager.getRequiredFleetConfiguration();

        view.refreshFleetSelection(placedCounts, requiredCounts);
    }
}
