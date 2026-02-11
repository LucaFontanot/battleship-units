package it.units.battleship.controller.game.actions;

import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;

import java.util.List;

/**
 * Interface defining the contract for sending data to the opponent via the network.
 * Acts as an intermediate layer (Object to DTO) for outgoing communication,
 * abstracting the domain models from the network transport format.
 */
public interface NetworkActionsSender {
    /**
     * Sends the current game status and an optional message to the opponent.
     */
    void sendGameStatus(GameState gameState, String message);

    /**
     * Sends a request to take a shot at the specified coordinate to the opponent.
     */
    void sendShotRequest(Coordinate coordinate);

    /**
     * Sends a grid update to the opponent, including the current grid state,
     * the list of sunk ships, and the outcome of the last shot.
     */
    void sendGridUpdate(Grid grid, List<Ship> fleet, boolean shotOutcome);
}