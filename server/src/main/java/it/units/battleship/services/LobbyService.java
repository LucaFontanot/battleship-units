package it.units.battleship.services;

import io.javalin.websocket.WsConfig;
import it.units.battleship.models.Lobby;

import java.util.HashMap;
import java.util.List;

/**
 * LobbyService class that manages game lobbies.
 */
public class LobbyService {
    final HashMap<String, Lobby> lobbies = new HashMap<>();

    /**
     * Returns a list of all lobbies.
     * @return list of all lobbies
     */
    public List<Lobby> getAllLobbies() {
        return lobbies.values().stream().toList();
    }

    /**
     * Returns a list of available lobbies (lobbies without a second player).
     * @return list of available lobbies
     */
    public List<Lobby> getAvailableLobbies() {
        return lobbies.values().stream().filter(lobby -> lobby.getPlayerTwo() == null).toList();
    }

    /**
     * Returns a lobby by its ID.
     * @param lobbyID the ID of the lobby
     * @return the lobby with the specified ID, or null if not found
     */
    public Lobby getLobbyByID(String lobbyID) {
        return lobbies.get(lobbyID);
    }

    /**
     * Adds a new lobby.
     * @param lobby the lobby to add
     */
    public void addLobby(Lobby lobby) {
        lobbies.put(lobby.getLobbyID(), lobby);
    }

    /**
     * Removes a lobby by its ID.
     * @param lobbyID the ID of the lobby to remove
     */
    public void removeLobby(String lobbyID) {
        lobbies.remove(lobbyID);
    }

    /**
     * Connects player two to a lobby.
     * @param lobbyID the ID of the lobby
     * @param playerTwo the WebSocket configuration of player two
     */
    public void connectPlayerTwo(String lobbyID, WsConfig playerTwo) {
        Lobby lobby = lobbies.get(lobbyID);
        if (lobby != null) {
            lobby.setPlayerTwo(playerTwo);
        }
    }

    /**
     * Disconnects player one from a lobby.
     * @param lobbyID the ID of the lobby
     */
    public void disconnectPlayerOne(String lobbyID) {
        Lobby lobby = lobbies.get(lobbyID);
        if (lobby != null) {
            lobby.setPlayerOne(null);
            if (lobby.getPlayerTwo() == null) {
                lobbies.remove(lobbyID);
            }
        }
    }

    /**
     * Disconnects player two from a lobby.
     * @param lobbyID the ID of the lobby
     */
    public void disconnectPlayerTwo(String lobbyID) {
        Lobby lobby = lobbies.get(lobbyID);
        if (lobby != null) {
            lobby.setPlayerTwo(null);
            if (lobby.getPlayerOne() == null) {
                lobbies.remove(lobbyID);
            }
        }
    }
}
