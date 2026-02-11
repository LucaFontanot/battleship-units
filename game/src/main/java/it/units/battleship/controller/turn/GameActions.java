package it.units.battleship.controller.turn;

import it.units.battleship.CellState;
import it.units.battleship.Coordinate;
import it.units.battleship.model.Ship;

import java.util.LinkedHashSet;
import java.util.List;

public interface GameActions {
    // ==== Model query =====
    CellState getOpponentCellState(Coordinate coordinate);

    // ==== Game actions ====
    void placeShip(Coordinate coordinate);
    void previewPlacement(Coordinate coordinate);
    void fireShot(Coordinate coordinate);
    boolean processIncomingShot(Coordinate coordinate);

    // ==== View =====
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

    // ==== Network ====
    void sendGameOver(String message);

    // ==== State transition ====
    void transitionToActiveTurn();
    void transitionToWaitingOpponent();
    void transitionToWaitingSetup();
    void transitionToGameOver(boolean won, String message);
}
