package it.units.battleship.controller.turn.states;

import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.controller.turn.GameContext;
import it.units.battleship.model.Ship;

import java.util.LinkedHashSet;

/**
 * Represents the Setup phase where players can place their ships on the grid.
 */
public class SetupState extends BaseGameState {

    public SetupState(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        view.syncFleetAvailabilityUI();
        view.setPlayerTurn(true);
    }

    @Override
    public void handlePlayerGridClick(Coordinate coordinate) {
        Orientation orientation = view.getSelectedOrientation();
        ShipType shipType = view.getSelectedShipType();

        if (shipType == null) return;

        try {
            Ship ship = Ship.createShip(coordinate, orientation, shipType, fleetManager.getGrid());
            boolean placed = fleetManager.addShip(ship);

            if (placed) {
                view.refreshPlayerGrid();
                view.syncFleetAvailabilityUI();

                if (fleetManager.isFleetComplete()) {
                    stateTransitions.transitionToWaitingSetup();
                    network.notifySetupComplete();
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
    public void handlePlayerGridHover(Coordinate coordinate) {
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
