package it.units.battleship.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.http.Context;
import io.javalin.json.JavalinGson;
import it.units.battleship.WebServerApp;
import it.units.battleship.models.Lobby;
import it.units.battleship.models.LobbySerializer;
import lombok.Getter;

/**
 * AbstractRoute class that serves
 */
public abstract class AbstractRoute <T> {

    @Getter
    final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Lobby.class, new LobbySerializer())
            .setPrettyPrinting()
            .create();
    @Getter
    final WebServerApp app;

    /**
     * Constructor for AbstractRoute.
     *
     * @param app the WebServerApp instance
     */
    public AbstractRoute(WebServerApp app) {
        this.app = app;
    }

    /**
     * Returns the service associated with the route.
     * @return the service of type T
     */
    public T getService() {
        return null;
    }

    /**
     * Returns the route path.
     *
     * @return the route path as a String
     */
    public String getRoutePath() {
        return "/";
    }

    /**
     * Handles GET requests.
     * @param ctx the Javalin context
     */
    public void handleGetRequest(Context ctx) {
        ctx.status(501).result("Not Implemented");
    }

    /**
     * Handles POST requests.
     * @param ctx the Javalin context
     */
    public void handlePostRequest(Context ctx) {
        ctx.status(501).result("Not Implemented");
    }

    /**
     * Handles PUT requests.
     * @param ctx the Javalin context
     */
    public void handlePutRequest(Context ctx) {
        ctx.status(501).result("Not Implemented");
    }

    /**
     * Handles DELETE requests.
     * @param ctx the Javalin context
     */
    public void handleDeleteRequest(Context ctx) {
        ctx.status(501).result("Not Implemented");
    }

    /**
     * Handles PATCH requests.
     * @param ctx the Javalin context
     */
    public void handlePatchRequest(Context ctx) {
        ctx.status(501).result("Not Implemented");
    }
}
