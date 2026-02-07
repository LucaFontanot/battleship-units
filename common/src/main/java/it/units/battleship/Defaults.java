package it.units.battleship;

public class Defaults {
    public static final String HTTP_SERVER_HOST = "http://localhost";
    public static final int HTTP_SERVER_PORT = 80;

    public static final String HTTP_LOBBY_PATH = "/api/lobbies";
    public static final String HTTP_LOBBY_ENDPOINT = HTTP_SERVER_HOST + ":" + HTTP_SERVER_PORT + HTTP_LOBBY_PATH;

    public static final String HTTP_PING_PATH = "/api/ping";
    public static final String HTTP_PING_ENDPOINT = HTTP_SERVER_HOST + ":" + HTTP_SERVER_PORT + HTTP_PING_PATH;
}
