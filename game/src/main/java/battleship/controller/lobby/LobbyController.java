package battleship.controller.lobby;

import battleship.controller.http.JsonHttpController;
import battleship.controller.http.JsonHttpException;
import battleship.controller.network.NetworkClient;
import it.units.battleship.Defaults;
import it.units.battleship.Logger;
import it.units.battleship.data.LobbiesResponseData;
import it.units.battleship.data.LobbyCreateRequestData;
import it.units.battleship.data.LobbyData;
import it.units.battleship.data.PingResponseData;
import it.units.battleship.data.socket.WebSocketAuthenticationRequest;
import it.units.battleship.data.socket.WebSocketMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class LobbyController {
    final JsonHttpController<PingResponseData, Void> pingResponse = new JsonHttpController<>(PingResponseData.class, Void.class);
    final JsonHttpController<LobbiesResponseData, Void> lobbiesController = new JsonHttpController<>(LobbiesResponseData.class, Void.class);
    final JsonHttpController<LobbyData, Void> lobbyCreateController = new JsonHttpController<>(LobbyData.class, Void.class);

    /**
     * Pings the server to check if it's alive and retrieves the ping response data.
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
     * @param name the name of the lobby to be created
     * @param player the name of the player creating the lobby
     * @return the LobbyData of the created lobby, or null if an error occurs
     */
    public LobbyData createLobby(String name, String player) {
        try {
            LobbyCreateRequestData requestData = LobbyCreateRequestData.builder()
                    .name(name)
                    .player(player)
                    .build();
            return lobbyCreateController.postSync(Defaults.HTTP_LOBBY_ENDPOINT, requestData);
        } catch (JsonHttpException e) {
            Logger.error("Failed to create lobby: " + e.getMessage());
            Logger.exception(e);
            return null;
        }
    }

    public NetworkClient connectLobbyWebsocket(LobbyData data, String playerName) {
        return new NetworkClient(data, playerName);
    }
}
