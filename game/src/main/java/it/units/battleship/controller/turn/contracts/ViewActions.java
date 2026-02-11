package it.units.battleship.controller.turn.contracts;

import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import it.units.battleship.model.Ship;

import java.util.LinkedHashSet;
import java.util.List;

public interface ViewActions {
    void setPlayerTurn(boolean isPlayerTurn);
    void notifyUser(String message);
    void refreshPlayerGrid();
    void refreshFleetUI();
    void showShotPreview(Coordinate coordinate);
    void showEndGame(String game);
    void transitionToGamePhase();
    void updateOpponentGrid(String grid, List<Ship> fleet);
    void playerErrorSound();
    void showPlacementPreview(LinkedHashSet<Coordinate> coordinates, boolean valid, Ship ship);

    ShipType getSelectedShipType();
    Orientation getSelectedOrientation();
}
