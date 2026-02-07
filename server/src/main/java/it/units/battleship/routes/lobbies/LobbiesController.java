package it.units.battleship.routes.lobbies;

import io.javalin.http.Context;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;
import it.units.battleship.Defaults;
import it.units.battleship.WebServerApp;
import it.units.battleship.data.LobbiesResponseData;
import it.units.battleship.data.LobbyCreateRequestData;
import it.units.battleship.data.LobbyData;
import it.units.battleship.impl.AbstractRoute;

import java.util.HashMap;
import java.util.UUID;

public class LobbiesController extends AbstractRoute<LobbiesService> {

    /**
     * Constructor for AbstractRoute.
     *
     * @param app the WebServerApp instance
     */
    public LobbiesController(WebServerApp app) {
        super(app);
    }

    @Override
    public LobbiesService getService() {
        return getApp().getLobbiesService();
    }

    @Override
    public String getRoutePath() {
        return Defaults.HTTP_LOBBY_PATH;
    }

    @Override
    public void handleGetRequest(Context ctx) {
        LobbiesResponseData data = LobbiesResponseData.builder()
                .count(getService().getAllLobbies().size())
                .results(getService().getAvailableLobbies())
                .build();
        ctx.status(200).result(getApp().getGson().toJson(data, LobbiesResponseData.class));
    }

    @Override
    public void handlePostRequest(Context ctx) {
        LobbyCreateRequestData requestData = getApp().getGson().fromJson(ctx.body(), LobbyCreateRequestData.class);
        LobbyData newLobby = LobbyData.builder()
                .lobbyID(UUID.randomUUID().toString())
                .lobbyName(requestData.getName())
                .playerOne(requestData.getPlayer())
                .build();
        getService().addLobby(newLobby);
        ctx.status(201).result(getApp().getGson().toJson(newLobby, LobbyData.class));
    }


    HashMap<WsContext, LobbySocketClient> websocketClients = new HashMap<>();

    @Override
    public void handleWebsocketRequest(WsConfig config) {
        config.onConnect(ctx -> {
            LobbySocketClient client = new LobbySocketClient(getApp(), ctx);
            websocketClients.put(ctx, client);
            client.onConnect(ctx);
        });
        config.onClose(ctx -> {
            LobbySocketClient client = websocketClients.get(ctx);
            if (client != null) {
                client.onClose(ctx);
            }
        });
        config.onBinaryMessage(ctx -> {
            LobbySocketClient client = websocketClients.get(ctx);
            if (client != null) {
                client.onBinaryMessage(ctx);
            }
        });
        config.onError(ctx -> {
            LobbySocketClient client = websocketClients.get(ctx);
            if (client != null) {
                client.onError(ctx);
            }
        });
        config.onMessage(ctx -> {
            LobbySocketClient client = websocketClients.get(ctx);
            if (client != null) {
                client.onMessage(ctx);
            }
        });

    }
}
