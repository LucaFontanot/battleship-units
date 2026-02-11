package it.units.battleship.controller.game.events;

import it.units.battleship.data.socket.payloads.GameConfigDTO;
import it.units.battleship.data.socket.payloads.GameStatusDTO;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;

/**
 * Interface that defines the callbacks triggered when raw data (DTOs) is received from the network.
 * This acts as the entry point for incoming network messages before they are
 * translated into domain objects by handlers.
 */
public interface CommunicationEvents {

    /**
     * Triggered when a chat message or a generic text message is received.
     *
     * @param playerName the name of the sender
     * @param message    the content of the message
     */
    void onPlayerMessage(String playerName, String message);

    /**
     * Triggered when an update about the opponent's grid state is received.
     *
     * @param gridUpdateDTO the data transfer object containing the new grid state and sunk ships
     */
    void onOpponentGridUpdate(GridUpdateDTO gridUpdateDTO);

    /**
     * Triggered when a shot request is received from the opponent.
     *
     * @param shotRequestDTO the data transfer object containing the targeted coordinates
     */
    void onShotReceived(ShotRequestDTO shotRequestDTO);

    /**
     * Triggered when game configuration/setup data is received.
     *
     * @param gameConfigDTO the data transfer object containing initial game settings
     */
    void onGameSetupReceived(GameConfigDTO gameConfigDTO);

    /**
     * Triggered when a general game status update (e.g., turn change, game over) is received.
     *
     * @param gameStatusDTO the data transfer object containing the new game state and status message
     */
    void onGameStatusReceived(GameStatusDTO gameStatusDTO);
}
