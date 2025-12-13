package it.units.battleship.routes.ping;

import io.javalin.http.Context;
import it.units.battleship.data.PingResponseData;
import it.units.battleship.impl.AbstractRoute;

/**
 * PingController class that handles ping requests.
 */
public class PingController extends AbstractRoute<PingService> {
    final PingService service = new PingService();

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
        return "/api/ping";
    }

    /**
     * Handles GET requests.
     * @param ctx the Javalin context
     */
    @Override
    public void handleGetRequest(Context ctx) {
        ctx.status(200).result(getGson().toJsonString(getService().getPingResponse(), PingResponseData.class));
    }
}
