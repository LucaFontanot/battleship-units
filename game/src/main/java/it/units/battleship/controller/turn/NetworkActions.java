package it.units.battleship.controller.turn;

import it.units.battleship.Coordinate;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;

import java.util.List;

public interface NetworkActions {
    void fireShot(Coordinate coordinate);
    void sendGameOver(String message);
    void notifySetupComplete();
    void sendGridUpdate(Grid grid, List<Ship> fleet, boolean hit);
}
