package it.units.battleship.models;

import it.units.battleship.data.LobbyData;
import it.units.battleship.routes.lobbies.LobbySocketClient;
import lombok.Getter;
import lombok.Setter;

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

    /**
     * Checks if both players in the lobby are ready.
     *
     * @return returns true if both playerOneReady and playerTwoReady are true, indicating that both players have indicated they are ready to start the game.
     */
    public boolean areBothReady() {
        return playerOneReady && playerTwoReady;
    }
}
