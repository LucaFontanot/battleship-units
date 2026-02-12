package it.units.battleship.controller.turn.contracts;

import it.units.battleship.Coordinate;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;

import java.util.List;

/**
 * Responsibility: Handles sendings of data to the remote opponent player.
 */
public interface NetworkActions {
    /** send a shoot coordinate to opponent */
    void fireShot(Coordinate coordinate);
    /** notify the opponent that the game is over */
    void sendGameOver(String message);
    /** say to opponent we are redy with ships */
    void notifySetupComplete();
    /** sync the current grid and fleet state */
    void sendGridUpdate(Grid grid, List<Ship> fleet, boolean hit);
}
