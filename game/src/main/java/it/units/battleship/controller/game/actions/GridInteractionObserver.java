package it.units.battleship.controller.game.actions;

import it.units.battleship.Coordinate;

/**
 * Observer interface for monitoring user interactions with the game grid.
 * Used to notify determined components when a cell is hovered or click
 */
public interface GridInteractionObserver {

    /**
     * Called when the mouse hovers over a grid cell.
     */
    void onGridHover(Coordinate coordinate);

    /**
     * Called when a grid cell is clicked.
     */
    void onGridClick(Coordinate coordinate);
}
