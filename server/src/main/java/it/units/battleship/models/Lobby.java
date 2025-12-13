package it.units.battleship.models;

import io.javalin.websocket.WsConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Lobby class representing a game lobby with two players.
 */
@Getter
@Setter
public class Lobby {
    String lobbyID = UUID.randomUUID().toString();
    String lobbyName;
    WsConfig playerOne;
    WsConfig playerTwo;
}
