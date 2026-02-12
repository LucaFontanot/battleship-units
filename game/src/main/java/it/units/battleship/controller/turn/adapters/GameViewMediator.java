package it.units.battleship.controller.turn.adapters;

import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import it.units.battleship.controller.turn.contracts.SetupInputProvider;
import it.units.battleship.controller.turn.contracts.ViewActions;
import it.units.battleship.model.Ship;
import it.units.battleship.view.core.BattleshipView;
import lombok.NonNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class GameViewMediator implements ViewActions, SetupInputProvider {

    private final BattleshipView view;

    public GameViewMediator(@NonNull BattleshipView view) {
        this.view = view;
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
    public void refreshPlayerGrid(String gridSerialized, List<Ship> fleet) {
        view.updatePlayerGrid(gridSerialized, fleet);
    }

    @Override
    public void syncFleetAvailabilityUI(Map<ShipType, Integer> placed, Map<ShipType, Integer> required) {
        view.refreshFleetSelection(placed, required);
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
