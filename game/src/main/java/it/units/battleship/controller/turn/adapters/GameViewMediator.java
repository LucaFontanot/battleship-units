package it.units.battleship.controller.turn.adapters;

import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import it.units.battleship.controller.turn.contracts.ViewActions;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.view.core.BattleshipView;
import lombok.NonNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class GameViewMediator implements ViewActions {

    private final BattleshipView view;
    private final FleetManager fleetManager;
    private final Grid opponentGrid;

    public GameViewMediator(@NonNull BattleshipView view,
                            @NonNull FleetManager fleetManager,
                            @NonNull Grid opponentGrid) {
        this.view = view;
        this.fleetManager = fleetManager;
        this.opponentGrid = opponentGrid;
    }

    @Override
    public void setPlayerTurn(boolean isPlayerTurn) {
        view.setPlayerTurn(isPlayerTurn);
    }

    @Override
    public void notifyUser(String message) {
        view.showSystemMessage(message);
    }

    @Override
    public void refreshPlayerGrid() {
        String gridSerialized = fleetManager.getSerializedGridState();
        view.updatePlayerGrid(gridSerialized, fleetManager.getFleet());
    }

    @Override
    public void syncFleetAvailabilityUI() {
        Map<ShipType, Integer> placedCounts = fleetManager.getPlacedCounts();
        Map<ShipType, Integer> requiredCounts = fleetManager.getRequiredFleetConfiguration();
        view.refreshFleetSelection(placedCounts, requiredCounts);

    }

    @Override
    public void showShotPreview(Coordinate coordinate) {
        view.showShotPreview(coordinate);
    }

    @Override
    public void showEndGame(String message) {
        view.showEndGamePhase(message);
    }

    @Override
    public void transitionToGamePhase() {
        view.transitionToGamePhase();
    }

    @Override
    public void updateOpponentGrid(String grid, List<Ship> fleet) {
        opponentGrid.updateGridState(grid);
        view.updateOpponentGrid(grid, fleet);
    }

    @Override
    public void playerErrorSound() {
        view.playerErrorSound();
    }

    @Override
    public void showPlacementPreview(LinkedHashSet<Coordinate> coordinates, boolean valid, Ship ship) {
        view.showPlacementPreview(coordinates, valid, ship);
    }

    @Override
    public ShipType getSelectedShipType() {
        return view.getSelectedShipType();
    }

    @Override
    public Orientation getSelectedOrientation() {
        return view.getSelectedOrientation();
    }
}
