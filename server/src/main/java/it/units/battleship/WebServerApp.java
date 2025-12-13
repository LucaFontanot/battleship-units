package it.units.battleship;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import it.units.battleship.impl.AbstractRoute;
import it.units.battleship.models.Lobby;
import it.units.battleship.models.LobbySerializer;
import it.units.battleship.routes.lobbies.LobbiesController;
import it.units.battleship.routes.lobbies.LobbiesService;
import it.units.battleship.routes.ping.PingController;
import lombok.Getter;

import java.time.Duration;

/**
 * WebServerApp class that initializes and starts a Javalin web server.
 */
public class WebServerApp extends Thread {
    @Getter
    final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Lobby.class, new LobbySerializer())
            .setPrettyPrinting()
            .create();
    @Getter
    final Javalin app;
    final int port;
    @Getter
    final LobbiesService lobbiesService = new LobbiesService();

    final AbstractRoute<?>[] routes = new AbstractRoute<?>[]{
            new PingController(this),
            new LobbiesController(this),
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
            config.router.mount( router -> {
                router.before(ctx -> {
                    Logger.log(String.format("[%s] %s %s", ctx.method(), ctx.path(), ctx.ip()));
                });
                for (AbstractRoute<?> route : routes) {
                    router.get(route.getRoutePath(), route::handleGetRequest);
                    router.post(route.getRoutePath(), route::handlePostRequest);
                    router.put(route.getRoutePath(), route::handlePutRequest);
                    router.delete(route.getRoutePath(), route::handleDeleteRequest);
                    router.ws(route.getRoutePath(), route::handleWebsocketRequest);
                }
            });
        });
    }

    /**
     * Starts the Javalin web server on the specified port.
     */
    @Override
    public void run() {
        app.start(port);
    }

    /**
     * Stops the Javalin web server.
     */
    public void close(){
        app.stop();
    }
}
