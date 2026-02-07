package battleship.controller.ai;

import battleship.model.FleetManager;
import battleship.model.Grid;
import it.units.battleship.Coordinate;

public class SimpleAIOpponent implements AIOpponent{
    private final Grid grid;
    private final FleetManager fleetManager;

    public SimpleAIOpponent(Grid grid, FleetManager fleetManager) {
        this.grid = grid;
        this.fleetManager = fleetManager;
    }

    @Override
    public void placeShips() {

    }

    @Override
    public Coordinate calculateNextShot() {
        return null;
    }

    @Override
    public void processLastShotResult(boolean hit) {

    }
}
