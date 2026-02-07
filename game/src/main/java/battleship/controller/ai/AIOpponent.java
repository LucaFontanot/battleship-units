package battleship.controller.ai;

import it.units.battleship.Coordinate;

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
    void processLastShotResult(boolean hit);
}
