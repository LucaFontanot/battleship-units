package it.units.battleship.data.socket;

import lombok.*;

/**
 * A generic class representing a WebSocket message.
 *
 * @param <T> the type of data that the message contains
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebSocketMessage <T> {
    String type;
    T data;
}