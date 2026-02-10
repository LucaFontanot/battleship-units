package battleship.controller.game.actions;

import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;

import java.util.List;

/**
 * Interface for the game logic to react to network-triggered events.
 * It serves as the final entry point for network data into the game strategy
 */
public interface NetworkActionsReceiver {
    /**
     * Triggered when a shot is received from the opponent.
     */
    void processIncomingShot(Coordinate coordinate);

    /**
     * Triggered when an opponent's grid update has been processed.
     * Receives the final serialized grid string and the list of {@link Ship} objects
     * that have been revealed/sunk.
     */
    void processOpponentGridUpdate(String grid, List<Ship> revealedFleet);

    /**
     * Triggered when a game state change has been received
     */
    void processGameStatusUpdate(GameState newState, String message);

}