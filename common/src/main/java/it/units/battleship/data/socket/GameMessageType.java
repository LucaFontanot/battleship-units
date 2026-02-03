package it.units.battleship.data.socket;

import lombok.Getter;

/**
 * Defines the protocol message types for Peer-to-Peer communication between players.
 * Since both players act as hosts/peers, these types synchronize actions and states
 * between the two game instances.
 */
@Getter
public enum GameMessageType {
    /**
     * Sent to negotiate or confirm initial game parameters (grid size, fleet rules).
     */
    GAME_SETUP("game_setup"),

    /**
     * Sent to synchronize grid state changes (e.g., after placement or a shot).
     */
    GRID_UPDATE("grid_update"),

    /**
     * Sent to notify the opponent of an incoming attack at specific coordinates.
     */
    SHOT_REQUEST("shot_request"),

    /**
     * Sent to signal the passing of move authority to the other player.
     */
    TURN_CHANGE("turn_change"),

    /**
     * Sent when a win/loss condition is detected to terminate the session.
     */
    GAME_OVER("game_over"),

    /**
     * Used for protocol errors or unexpected communication failures.
     */
    ERROR("error");

    private final String type;

    GameMessageType(String type){
        this.type = type;
    }
}
