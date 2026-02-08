package it.units.battleship.data.socket.payloads;

import it.units.battleship.GameState;

/**
 * A generic DTO used to exchange simple boolean states between peers.
 * Used for:
 * - TURN_CHANGE: true if it's the receiver's turn, false otherwise.
 * - GAME_OVER: true if the sender lost (notifying the receiver's victory), false otherwise.
 */
public record GameStatusDTO(
        GameState state,
        String message
) {}
