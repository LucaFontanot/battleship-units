package battleship.handlers;

import it.units.battleship.Coordinate;

/**
 * Interface for player communication handling.
 */
public interface CommunicationEvents {

    /**
     * Called when a player sends a message.
     * @param playerName the name of the player
     * @param message the message sent by the player
     */
    void onPlayerMessage(String playerName, String message);
    /**
     * Called when a player sends the coordinates of a shot during the game.
     *
     * @param playerName the name of the player who performed the shot
     * @param shotCoordinates the coordinates of the shot provided by the player
     */
    void onShotReceived(String playerName, Coordinate shotCoordinates);
}
