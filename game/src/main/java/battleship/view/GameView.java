package battleship.view;

import battleship.controller.GridInteractionObserver;
import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

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

    void setPlayerGridListener(GridInteractionObserver observer);
    void setOpponentGridListener(GridInteractionObserver observer);
    void refreshFleetSelection(Map<ShipType, Integer> shipCounts, Map<ShipType, Integer> fleetConfiguration);

    Orientation getSelectedOrientation();
    ShipType getSelectedShipType();

    void open();

    /**
     * Displays the setup phase of the Battleship game.
     *
     * During this phase, the player is expected to arrange their fleet on their grid.
     * Implementing classes should visually or textually indicate that the game is now in the setup phase.
     * This may involve rendering the player's grid, enabling ship placement functionality, or presenting
     * instructional messages to guide the player in placing their ships.
     */
    void showSetupPhase();

    /**
     * Displays the game phase of the Battleship game.
     *
     * This method is responsible for transitioning the visual or textual representation
     * of the game from the setup phase to the main game phase, where players take turns
     * attempting to sink each other's fleets. Implementing classes should indicate this
     * shift clearly to the user through appropriate updates to the interface.
     *
     * The implementation may include rendering both the player's and opponent's grids,
     * setting up the interface for turn-based interactions, and enabling necessary
     * UI elements (e.g., attack buttons, turn indicators).
     */
    void showGamePhase();

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

    void showSystemMessage(String message);

    void displayErrorAlert(String message);

    void showEndGamePhase(String winner);

    void displayShipSunk(Ship ship);

    /**
     * Sets the player's turn status in the game.
     *
     * This method is used to indicate whether it is currently the player's turn or not.
     * Implementing classes should visually or functionally adapt the game's interface
     * to reflect the turn status (e.g., enabling or disabling player controls).
     *
     * @param isPlayerTurn {@code true} if it is the player's turn, {@code false} otherwise
     */
    void setPlayerTurn(boolean isPlayerTurn);

    void showPlacementPreview(LinkedHashSet<Coordinate> coord, boolean validShip, Ship ship);

    void showShotPreview(Coordinate coord);
}
