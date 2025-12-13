package it.units.battleship.routes.lobbies;

import io.javalin.websocket.WsConfig;
import it.units.battleship.data.LobbyData;
import it.units.battleship.models.Lobby;

import java.util.HashMap;
import java.util.List;

/**
 * LobbyService class that manages game lobbies.
 */
public class LobbiesService {
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
        return lobbies.values().stream().filter(lobby -> lobby.getPlayerTwoCtx() == null).toList();
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
     * Adds a new lobby from LobbyData.
     * @param lobbyData the lobby data to create the lobby from
     */
    public void addLobby(LobbyData lobbyData) {
        Lobby lobby = new Lobby();
        lobby.setLobbyID(lobbyData.getLobbyID());
        lobby.setLobbyName(lobbyData.getLobbyName());
        lobby.setPlayerOne(lobbyData.getPlayerOne());
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
            lobby.setPlayerTwoCtx(playerTwo);
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
            if (lobby.getPlayerTwoCtx() == null) {
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
            if (lobby.getPlayerOneCtx() == null) {
                lobbies.remove(lobbyID);
            }
        }
    }
}
