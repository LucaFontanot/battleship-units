package battleship.controller.setup;

import battleship.controller.game.GameController;
import battleship.model.game.FleetManager;
import battleship.model.game.Ship;
import battleship.view.setup.SetupPanel;
import battleship.view.setup.SetupView;
import it.units.battleship.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SetupController implements SetupInteractionFacade {
    private final GameController gameController;
    private final SetupView view;
    private final FleetManager fleetManager;
    private final Consumer<FleetManager> onSetupComplete;


    public SetupController(GameController gameController, Consumer<FleetManager> onSetupComplete) {
        this.gameController = gameController;
        this.view = new SetupPanel(gameController);
        this.fleetManager = gameController.getFleetManager();
        this.onSetupComplete = onSetupComplete;

        view.setObserver(new SetupGridHandler(this));
    }

    public void startSetup() {
        view.open();
        view.updateShipButtons(fleetManager.getPlacedCounts(), fleetManager.getRequiredFleetConfiguration());
    }

    @Override
    public void requestShipPlacement(Coordinate coordinate) {
        handleSetupClick(coordinate);
    }

    @Override
    public void requestPlacementPreview(Coordinate coordinate) {
        handleSetupHover(coordinate);
    }

    @Override
    public void requestSetupCompletion() {
        onSetupComplete.accept(fleetManager);
    }

    /**
     * Handles the logic for placing a ship on the player's grid during the setup phase. It checks the selected ship type and orientation,
     *
     * @param coordinate the starting coordinate for the ship placement, and attempts to create and place the ship on the grid.
     */
    private void handleSetupClick(Coordinate coordinate) {
        Orientation selectedOrientation = view.getSelectedOrientation();
        ShipType selectedShipType = view.getSelectedShipType();

        Logger.debug("Attempting to place ship of type " + selectedShipType + " at coordinate " + coordinate + " with orientation " + selectedOrientation);

        if (selectedShipType == null) return;

        try {
            Ship ship = Ship.createShip(coordinate, selectedOrientation, selectedShipType, fleetManager.getGrid());
            boolean placed = fleetManager.addShip(ship);

            if (placed) {
                List<Ship> currentFleet = fleetManager.getFleet();

                view.updateSetupGrid(GridMapper.serialize(gameController.getGrid().getGrid()), currentFleet);

                Map<ShipType, Integer> shipCounts = fleetManager.getPlacedCounts();
                Map<ShipType, Integer> fleetConfiguration = fleetManager.getRequiredFleetConfiguration();

                view.updateShipButtons(shipCounts, fleetConfiguration);
            } else {
                view.playerErrorSound();
                view.showPlacementPreview(ship.getCoordinates(), false, ship);
            }
        } catch (IllegalArgumentException ex) {
            view.playerErrorSound();

            LinkedHashSet<Coordinate> coords = selectedShipType.getShipCoordinates(coordinate, selectedOrientation);
            view.showPlacementPreview(coords, false, null);
        }
    }

    private void handleSetupHover(Coordinate coordinate) {
        Orientation selectedOrientation = view.getSelectedOrientation();
        ShipType selectedShipType = view.getSelectedShipType();

        if (selectedShipType == null) return;
        try {
            Ship ship = Ship.createShip(coordinate, selectedOrientation, selectedShipType, fleetManager.getGrid());
            boolean valid = fleetManager.canPlaceShip(ship);

            view.showPlacementPreview(ship.getCoordinates(), valid, ship);

        } catch (IllegalArgumentException ex) {
            LinkedHashSet<Coordinate> coords = selectedShipType.getShipCoordinates(coordinate, selectedOrientation);
            view.showPlacementPreview(coords, false, null);
        }
    }
}
