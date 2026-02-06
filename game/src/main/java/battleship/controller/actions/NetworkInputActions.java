package battleship.controller.actions;

import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;

import java.util.List;

public interface NetworkInputActions {
    void processIncomingShot(Coordinate coordinate);
    void processOpponentGridUpdate(String grid, List<Ship> revealedFleet);
    void processGameStatusUpdate(GameState newState, String message);

}
