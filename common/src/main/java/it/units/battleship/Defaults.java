package it.units.battleship;

import java.util.Map;

public class Defaults {
    public static final String HTTP_SERVER_SCHEME = "https";
    public static final String WS_SERVER_SCHEME = "wss";
    public static final String HTTP_SERVER_HOST = "battleship.lucafontanot.it";
    public static final int HTTP_SERVER_PORT = 443;

    public static final String HTTP_LOBBY_PATH = "/api/lobbies";
    public static final String HTTP_LOBBY_ENDPOINT = HTTP_SERVER_SCHEME + "://" + HTTP_SERVER_HOST + ":" + HTTP_SERVER_PORT + HTTP_LOBBY_PATH;
    public static final String WEBSOCKET_LOBBY_ENDPOINT = WS_SERVER_SCHEME + "://" + HTTP_SERVER_HOST + ":" + HTTP_SERVER_PORT + HTTP_LOBBY_PATH;

    public static final String HTTP_PING_PATH = "/api/ping";
    public static final String HTTP_PING_ENDPOINT = HTTP_SERVER_SCHEME + "://" + HTTP_SERVER_HOST + ":" + HTTP_SERVER_PORT + HTTP_PING_PATH;


    public static final Map<ShipType, Integer> FLEET_CONFIGURATION = Map.of(
            ShipType.DESTROYER, 2,
            ShipType.FRIGATE, 2,
            ShipType.CRUISER, 1,
            ShipType.BATTLESHIP, 1,
            ShipType.CARRIER, 1
    );

    public static final int GRID_ROWS = 10;
    public static final int GRID_COLS = 10;

    public static final int MIN_DISTANCE_THRESHOLD = 1;

    public static final long DELAY_MS = 500;
}
