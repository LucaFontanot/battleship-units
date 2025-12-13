package it.units.battleship;

import io.javalin.Javalin;
import lombok.Getter;

import java.time.Duration;

/**
 * WebServerApp class that initializes and starts a Javalin web server.
 */
public class WebServerApp extends Thread {
    @Getter
    final Javalin app;
    final int port;

    /**
     * Constructor for WebServerApp.
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
     * Starts the Javalin web server on the specified port.
     */
    @Override
    public void run() {
        app.start(port);
    }
}
