package it.units.battleship.models;

import io.javalin.websocket.WsConfig;
import it.units.battleship.data.LobbyData;
import lombok.*;

/**
 * Lobby class representing a game lobby with two players.
 */
@Getter
@Setter
public class Lobby extends LobbyData {
    WsConfig playerOneCtx;
    WsConfig playerTwoCtx;
}
