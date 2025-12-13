package it.units.battleship.routes.lobbies;

import io.javalin.http.Context;
import it.units.battleship.WebServerApp;
import it.units.battleship.data.LobbiesResponseData;
import it.units.battleship.impl.AbstractRoute;

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
}
