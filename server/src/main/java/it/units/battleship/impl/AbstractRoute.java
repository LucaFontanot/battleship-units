package it.units.battleship.impl;

import io.javalin.http.Context;
import io.javalin.json.JavalinGson;
import lombok.Getter;

/**
 * AbstractRoute class that serves
 */
public abstract class AbstractRoute <T> {

    @Getter
    final JavalinGson gson = new JavalinGson();

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
