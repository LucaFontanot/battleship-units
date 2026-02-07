package battleship.controller.lobby;

import battleship.client.http.JsonHttpClient;
import battleship.client.http.JsonHttpException;
import battleship.controller.game.NetworkClient;
import it.units.battleship.Defaults;
import it.units.battleship.Logger;
import it.units.battleship.data.LobbiesResponseData;
import it.units.battleship.data.LobbyCreateRequestData;
import it.units.battleship.data.LobbyData;
import it.units.battleship.data.PingResponseData;
import lombok.SneakyThrows;

import java.util.function.Consumer;

public class LobbyController {
    final JsonHttpClient<PingResponseData, Void> pingResponse = new JsonHttpClient<>(PingResponseData.class, Void.class);
    final JsonHttpClient<LobbiesResponseData, Void> lobbiesController = new JsonHttpClient<>(LobbiesResponseData.class, Void.class);
    final JsonHttpClient<LobbyData, Void> lobbyCreateController = new JsonHttpClient<>(LobbyData.class, Void.class);

    final Consumer<NetworkClient> onLobbyJoin;

    public LobbyController(Consumer<NetworkClient> onLobbyJoin) {
        this.onLobbyJoin = onLobbyJoin;
    }

    /**
     * Pings the server to check if it's alive and retrieves the ping response data.
     *
     * @return the PingResponseData if the ping is successful, or null if an error occurs
     */
    public PingResponseData ping() {
        try {
            return pingResponse.getSync(Defaults.HTTP_PING_ENDPOINT);
        } catch (JsonHttpException e) {
            Logger.error("Failed to ping server: " + e.getMessage());
            Logger.exception(e);
            return null;
        }
    }

    /**
     * Retrieves the list of lobbies from the server.
     *
     * @return the LobbiesResponseData containing the list of lobbies, or null if an error occurs
     */
    public LobbiesResponseData getLobbies() {
        try {
            return lobbiesController.getSync(Defaults.HTTP_LOBBY_ENDPOINT);
        } catch (JsonHttpException e) {
            Logger.error("Failed to get lobbies: " + e.getMessage());
            Logger.exception(e);
            return null;
        }
    }

    /**
     * Creates a new lobby on the server with the given name and player.
     *
     * @param name   the name of the lobby to be created
     * @param player the name of the player creating the lobby
     * @return the LobbyData of the created lobby, or null if an error occurs
     */
    public LobbyData createLobby(String name, String player) {
        try {
            LobbyCreateRequestData requestData = LobbyCreateRequestData.builder().name(name).player(player).build();
            return lobbyCreateController.postSync(Defaults.HTTP_LOBBY_ENDPOINT, requestData);
        } catch (JsonHttpException e) {
            Logger.error("Failed to create lobby: " + e.getMessage());
            Logger.exception(e);
            return null;
        }
    }

    @SneakyThrows
    public boolean connectLobbyWebsocket(LobbyData data, String playerName) {
        NetworkClient client = new NetworkClient(data, playerName);
        Thread.sleep(500);
        if (client.isConnected() && client.isAuthenticated()) {
            onLobbyJoin.accept(client);
            return true;
        } else {
            Logger.error("Failed to connect to lobby websocket for lobby " + data.getLobbyName());
            return false;
        }
    }
}
