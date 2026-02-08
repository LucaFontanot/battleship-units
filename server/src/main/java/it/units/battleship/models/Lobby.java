package it.units.battleship.models;

import it.units.battleship.routes.lobbies.LobbySocketClient;
import it.units.battleship.data.LobbyData;
import lombok.*;

/**
 * Lobby class representing a game lobby with two players.
 */
@Getter
@Setter
public class Lobby extends LobbyData {
    LobbySocketClient playerOneCtx;
    LobbySocketClient playerTwoCtx;
    private boolean playerOneReady = false;
    private boolean playerTwoReady = false;

    public boolean areBothReady() {
        return playerOneReady && playerTwoReady;
    }
}
