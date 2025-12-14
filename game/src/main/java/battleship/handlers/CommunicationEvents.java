package battleship.handlers;

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
}
