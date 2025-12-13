package it.units.battleship.data.socket;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebSocketMessage <T> {
    String type;
    T data;
}