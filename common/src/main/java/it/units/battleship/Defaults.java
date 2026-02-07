package it.units.battleship;

public class Defaults {
    public static final String HTTP_SERVER_SCHEME = "http";
    public static final String WS_SERVER_SCHEME = "ws";
    public static final String HTTP_SERVER_HOST = "localhost";
    public static final int HTTP_SERVER_PORT = 8080;

    public static final String HTTP_LOBBY_PATH = "/api/lobbies";
    public static final String HTTP_LOBBY_ENDPOINT = HTTP_SERVER_SCHEME + "://" + HTTP_SERVER_HOST + ":" + HTTP_SERVER_PORT + HTTP_LOBBY_PATH;
    public static final String WEBSOCKET_LOBBY_ENDPOINT = WS_SERVER_SCHEME + "://" + HTTP_SERVER_HOST + ":" + HTTP_SERVER_PORT + HTTP_LOBBY_PATH;

    public static final String HTTP_PING_PATH = "/api/ping";
    public static final String HTTP_PING_ENDPOINT = HTTP_SERVER_SCHEME + "://" + HTTP_SERVER_HOST + ":" + HTTP_SERVER_PORT + HTTP_PING_PATH;
}
