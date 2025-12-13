package it.units.battleship.routes.lobbies;

import io.javalin.http.Context;
import it.units.battleship.WebServerApp;
import it.units.battleship.data.LobbiesResponseData;
import it.units.battleship.data.LobbyCreateRequestData;
import it.units.battleship.data.LobbyData;
import it.units.battleship.impl.AbstractRoute;
import it.units.battleship.models.Lobby;

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
        return "/api/lobbies";
    }

    @Override
    public void handleGetRequest(Context ctx) {
        LobbiesResponseData data = LobbiesResponseData.builder()
                .count(getService().getAllLobbies().size())
                .results(getService().getAvailableLobbies())
                .build();
        ctx.status(200).result(getGson().toJson(data, LobbiesResponseData.class));
    }

    @Override
    public void handlePostRequest(Context ctx) {
        LobbyCreateRequestData requestData = getGson().fromJson(ctx.body(), LobbyCreateRequestData.class);
        LobbyData newLobby = LobbyData.builder()
                .lobbyID(UUID.randomUUID().toString())
                .lobbyName(requestData.getName())
                .playerOne(requestData.getPlayerOne())
                .build();
        getService().addLobby(newLobby);
        ctx.status(201).result(getGson().toJson(newLobby, LobbyData.class));
    }
}
