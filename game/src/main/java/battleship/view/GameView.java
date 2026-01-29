package battleship.view;

import battleship.model.Ship;

import java.util.List;

/**
 * Defines the contract between the Game Controller and the User Interface (View).
 * This interface abstracts the visual presentation layer, allowing the Controller to
 * update the game state without direct dependency on Swing or specific GUI implementations.
 *
 * Key responsibilities:
 *  - Rendering the current state of the player's grid (ships and hits received).
 *  - Rendering the known state of the opponent's grid (shots fired and their results).
 */
public interface GameView {
    /**
     * Refreshes the visualization of the local player's grid.
     * This includes displaying the player's fleet positions and the status of any incoming attacks
     * (e.g., hits, misses, sunk ships).
     *
     * @param gridSerialized A string representation of the grid cells.
     * @param fleetToRender The list of the player's ships to overlay onto the grid.
     */
    void updatePlayerGrid(String gridSerialized, List<Ship> fleetToRender);

    /**
     * Refreshes the visualization of the opponent's grid.
     * This view is typically limited to showing the results of the player's shots (hits/misses)
     * and revealed sunken ships, without exposing the opponent's full fleet layout unless ships are sunk.
     *
     * @param gridSerialized A string representation of the grid cells.
     * @param fleetToRender The list of revealed opponent ships (only sunk ones).
     */
    void updateOpponentGrid(String gridSerialized, List<Ship> fleetToRender);

}
