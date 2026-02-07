package it.units.battleship.routes.ping;

import io.javalin.http.Context;
import it.units.battleship.Defaults;
import it.units.battleship.WebServerApp;
import it.units.battleship.data.PingResponseData;
import it.units.battleship.impl.AbstractRoute;

/**
 * PingController class that handles ping requests.
 */
public class PingController extends AbstractRoute<PingService> {
    final PingService service = new PingService();

    /**
     * Constructor for AbstractRoute.
     *
     * @param app the WebServerApp instance
     */
    public PingController(WebServerApp app) {
        super(app);
    }

    /**
     * Returns the PingService associated with the route.
     * @return the PingService
     */
    @Override
    public PingService getService() {
        return service;
    }

    /**
     * Returns the route path.
     *
     * @return the route path as a String
     */
    public String getRoutePath() {
        return Defaults.HTTP_PING_PATH;
    }

    /**
     * Handles GET requests.
     * @param ctx the Javalin context
     */
    @Override
    public void handleGetRequest(Context ctx) {
        ctx.status(200).result(getApp().getGson().toJson(getService().getPingResponse(), PingResponseData.class));
    }
}
