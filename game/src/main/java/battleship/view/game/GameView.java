package battleship.view.game;

import battleship.controller.game.actions.GridInteractionObserver;
import battleship.model.game.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.ShipType;

import java.util.List;
import java.util.Map;

/**
 * Represents the view interface for the Battleship game.
 * <p>
 * This interface defines the contract that any implementing class must fulfill to
 * visually represent and manage the user interface elements of the game, including
 * rendering grids, handling player and opponent interactions, and facilitating the
 * different phases of the game.
 */

public interface GameView {

    /**
     * Sets the listener for the player's grid interactions.
     * The specified {@link GridInteractionObserver} will handle events such as hovering over
     * or clicking grid cells on the player's grid.
     *
     * @param observer the observer that monitors and responds to interactions
     *                 with the player's grid cells
     */
    void setPlayerGridListener(GridInteractionObserver observer);

    /**
     * Sets the listener for the opponent's grid interaction events.
     * This method allows registering a {@link GridInteractionObserver} to handle user interactions
     * such as hovering or clicking on the opponent's grid during the game.
     *
     * @param observer the {@link GridInteractionObserver} instance that will handle events
     *                 on the opponent's grid (e.g., hover or click actions).
     *                 Passing {@code null} removes any previously set listener.
     */
    void setOpponentGridListener(GridInteractionObserver observer);

    /**
     * Refreshes the fleet selection UI based on the updated ship counts and fleet configuration.
     * This method is typically invoked during the setup phase of the game to update the player's
     * available fleet information and reflect the current state of ship placement.
     *
     * @param shipCounts         A map containing count of each {@code ShipType} already
     *                           placed by the player. The key represents the ship type,
     *                           and the value represents the remaining counts for that type.
     * @param fleetConfiguration A map representing the player's current fleet configuration.
     *                           The key represents the {@code ShipType}, and the value represents
     *                           the maximum number of ships of that type that can be placed by the player.
     */
    void refreshFleetSelection(Map<ShipType, Integer> shipCounts, Map<ShipType, Integer> fleetConfiguration);



    void open();

    /**
     * Displays the game phase of the Battleship game.
     * <p>
     * This method is responsible for transitioning the visual or textual representation
     * of the game from the setup phase to the main game phase, where players take turns
     * attempting to sink each other's fleets. Implementing classes should indicate this
     * shift clearly to the user through appropriate updates to the interface.
     * <p>
     * The implementation may include rendering both the player's and opponent's grids,
     * setting up the interface for turn-based interactions, and enabling necessary
     * UI elements (e.g., attack buttons could be sufficient hover/click grid cell, turn indicators using setPlayerTurn()).
     */
    void showGamePhase();

    /**
     * Displays the end game phase message in the Battleship game.
     * <p>
     * This method is called to indicate the conclusion of the game and
     * typically displays a summary message reflecting the game's results,
     * such as whether the player won or lost, or any other end-game
     * information.
     *
     * @param message the message to be displayed, providing details
     *                about the outcome of the game or any concluding
     *                remarks for the player.
     */
    void showEndGamePhase(String message);

    /**
     * Refreshes the visualization of the local player's grid.
     * This includes displaying the player's fleet positions and the status of any incoming attacks
     * (e.g., hits, misses, sunk ships).
     *
     * @param gridSerialized A string representation of the grid cells.
     * @param fleetToRender  The list of the player's ships to overlay onto the grid.
     */
    void updatePlayerGrid(String gridSerialized, List<Ship> fleetToRender);

    /**
     * Refreshes the visualization of the opponent's grid.
     * This view is typically limited to showing the results of the player's shots (hits/misses)
     * and revealed sunken ships, without exposing the opponent's full fleet layout unless ships are sunk.
     *
     * @param gridSerialized A string representation of the grid cells.
     * @param fleetToRender  The list of revealed opponent ships (only sunk ones).
     */
    void updateOpponentGrid(String gridSerialized, List<Ship> fleetToRender);

    void showSystemMessage(String message);

    void displayErrorAlert(String message);

    /**
     * Sets the player's turn status in the game.
     * <p>
     * This method is used to indicate whether it is currently the player's turn or not.
     * Implementing classes should visually or functionally adapt the game's interface
     * to reflect the turn status (e.g., enabling or disabling player controls).
     *
     * @param isPlayerTurn {@code true} if it is the player's turn, {@code false} otherwise
     */
    void setPlayerTurn(boolean isPlayerTurn);

    /**
     * Shows a preview of the shot fired by the player.
     * <p>
     * This method is used to preview the grid cell targeted by the player's shot.
     *
     * @param coord the coordinate of the cell targeted by the shot
     */
    void showShotPreview(Coordinate coord);

    void playerErrorSound();
}
