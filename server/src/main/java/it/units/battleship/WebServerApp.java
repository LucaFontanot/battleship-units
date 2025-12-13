package it.units.battleship;

import io.javalin.Javalin;
import it.units.battleship.impl.AbstractRoute;
import it.units.battleship.routes.ping.PingController;
import lombok.Getter;

import java.time.Duration;

/**
 * WebServerApp class that initializes and starts a Javalin web server.
 */
public class WebServerApp extends Thread {
    @Getter
    final Javalin app;
    final int port;

    final AbstractRoute<?>[] routes = new AbstractRoute<?>[]{
            new PingController()
    };

    /**
     * Constructor for WebServerApp.
     *
     * @param port the port number on which the server will run
     */
    public WebServerApp(int port) {
        this.port = port;
        this.app = Javalin.create(config -> {
            config.jetty.modifyWebSocketServletFactory(factory -> {
                factory.setIdleTimeout(Duration.ofHours(1));
            });
        });
    }

    /**
     * Registers all routes with the Javalin application.
     */
    void registerRoutes() {
        for (AbstractRoute<?> route : routes) {
            app.get(route.getRoutePath(), route::handleGetRequest);
            app.post(route.getRoutePath(), route::handlePostRequest);
            app.put(route.getRoutePath(), route::handlePutRequest);
            app.delete(route.getRoutePath(), route::handleDeleteRequest);
        }
    }

    /**
     * Starts the Javalin web server on the specified port.
     */
    @Override
    public void run() {
        registerRoutes();
        app.start(port);
    }
}
