package battleship.controller.game.actions;

import it.units.battleship.Coordinate;

/**
 * Facade interface defining the actions a user can perform on the game grid via the UI.
 * It decouples the View from the Controller logic.
 */
public interface GameInteractionFacade {
    /**
     * Requests a shot at the specified coordinate.
     * Used when the player clicks on an opponent's cell.
     */
    void requestShot(Coordinate coordinate);

    /**
     * Shows a preview of a shot at the specified coordinate.
     * Used for visual feedback when the player hovers over a cell on opponent's grid.
     */
    void previewShot(Coordinate coordinate);

    /**
     * Requests the placement of a ship at the specified coordinate.
     * Used for setup phase when the player clicks to place a ship.
     */
    void requestShipPlacement(Coordinate coordinate);

    /**
     * Provides a preview of ship placement at the specified coordinate.
     */
    void requestPlacementPreview(Coordinate coordinate);
}
