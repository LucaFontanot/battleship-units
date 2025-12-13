package it.units.battleship.data;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LobbyCreateRequestData {
    String name;
    String playerOne;
}
