package battleship.handlers;

import it.units.battleship.Coordinate;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;

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
     * @param gridUpdateDTO
     */
    void onOpponentGridUpdate(GridUpdateDTO gridUpdateDTO);

    void onShotReceived(ShotRequestDTO shotRequestDTO);
}
