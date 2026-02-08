package battleship.controller.mode;


import battleship.model.game.Grid;
import battleship.model.game.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;

import java.util.List;

/**
 * Strategy for the different game modes.
 * Defines how to communicate with the opponent
 */
public interface GameModeStrategy {

    /**
     * Initialize the game mode.
     * For local multiplayer: enable the connection
     * For single player: initialize the game
     */
    void initialize(GameModeCallback callback);

    /**
     * Sends a shot to the opponent
     */
    void sendShot(Coordinate coordinate);

    /**
     * Send the grid update to the opponent after taking a shot.
     */
    void sendGridUpdate(Grid grid, List<Ship> fleet, boolean shotOutcome);

    /**
     * Notify the opponent that the game is over.
     */
    void sendGameOver(String message);

    /**
     * Notify the opponent that the game setup is complete.
     */
    void notifySetupComplete();

    /**
     * Close the connection with the opponent.
     */
    void shutdown();

    /**
     * Returns the name of the game mode.
     */
    String getModeName();

    /**
     * Callback interface for game mode events.
     */
    interface GameModeCallback {
        void onOpponentReady();
        void onShotReceived(Coordinate coordinate);
        void onGridUpdateReceived(String gridSerialized, List<Ship> fleet);
        void onGameStatusReceived(GameState state, String message);
        void onConnectionError(String error);
    }
}
