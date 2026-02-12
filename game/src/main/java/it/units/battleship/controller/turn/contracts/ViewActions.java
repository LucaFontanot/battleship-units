package it.units.battleship.controller.turn.contracts;

import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import it.units.battleship.model.Ship;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * Responsibility: Manage the UI and what user see on screen.
 */
public interface ViewActions {
    /** change turn indicator for player */
    void setPlayerTurn(boolean isPlayerTurn);
    /** show a pop up or text message */
    void notifyUser(String message);
    /** repaint the player board */
    void refreshPlayerGrid();
    /** update the fleet list view */
    void refreshFleetUI();
    /** show where shot will land */
    void showShotPreview(Coordinate coordinate);
    /** display final game result screen */
    void showEndGame(String game);
    /** switch from setup to fight phase */
    void transitionToGamePhase();
    /** update the enemy grid view */
    void updateOpponentGrid(String grid, List<Ship> fleet);
    /** play a bad sound when error */
    void playerErrorSound();
    /** show ship ghost before place it */
    void showPlacementPreview(LinkedHashSet<Coordinate> coordinates, boolean valid, Ship ship);

    /** get which ship is select in UI */
    ShipType getSelectedShipType();
    /** get the current rotate orientation */
    Orientation getSelectedOrientation();
}
