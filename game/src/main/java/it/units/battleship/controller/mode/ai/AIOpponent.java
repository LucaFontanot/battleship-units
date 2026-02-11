package it.units.battleship.controller.mode.ai;

import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.Coordinate;

import java.util.List;

/**
 * Interface for AI opponents.
 */
public interface AIOpponent {

    /**
     * Automatically place the ships on the grid.
     */
    void placeShips();

    /**
     * Compute the next move.
     */
    Coordinate calculateNextShot();

    /**
     * Process the outcome of the last shot.
     */
    void processLastShotResult(Grid grid, List<Ship> fleet, boolean shotOutcome);
}
