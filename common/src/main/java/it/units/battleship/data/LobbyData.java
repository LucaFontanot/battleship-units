package it.units.battleship.data;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LobbyData {
    String lobbyID = UUID.randomUUID().toString();
    String lobbyName;
    String playerOne;
    String playerTwo;
}
