package it.units.battleship.data.socket;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebSocketAuthenticationRequest {
    String id;
    String name;
}
