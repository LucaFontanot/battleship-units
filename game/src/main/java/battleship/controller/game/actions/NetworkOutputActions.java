package battleship.controller.game.actions;

import battleship.model.Grid;
import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;

import java.util.List;

public interface NetworkOutputActions {
    void sendGameStatus(GameState gameState, String message);
    void sendShotRequest(Coordinate coordinate);
    void sendGridUpdate(Grid grid, List<Ship> fleet, boolean shotOutcome);
}
