package battleship.view;

import battleship.model.Ship;

import java.util.List;

/**
 * Represents the view component of the Battleship game, responsible for displaying
 * the game's state and relaying information to the user. This interface defines methods
 * for updating the game interface with player and opponent grid states, messages, and
 * game-over notifications.
 *
 * Implementing classes should provide specific visual or textual representations
 * of the game state depending on the application's requirements (e.g., a graphical user
 * interface or a text-based command-line interface).
 */

public interface GameView {

    void show();

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
     * Updates the player's grid representation and fleet information in the game view.
     *
     * This method is used to refresh the visual or textual representation of the player's grid,
     * reflecting the current state of the grid and the fleet placements. It is typically called
     * after the player's grid or fleet has been modified, such as during ship placement or
     * after an attack. Since the grid takes track only the hit status of each cell, for reder the fleet
     * over the grid it needs also the fleet list.
     *
     * @param serializedGrid the serialized representation of the player's grid, which encodes
     *                       the state of each cell (e.g., empty, occupied, hit, miss)
     * @param fleet          the list of ships currently placed on the player's grid, including
     *                       their locations and hit statuses
     */
    void updatePlayerGrid(String serializedGrid, List<Ship> fleet);

    void updateOpponentGrid(String serializedGrid);

    void updateSystemMessage(String message);

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

}
