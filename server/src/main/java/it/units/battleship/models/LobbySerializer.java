package it.units.battleship.models;

import com.google.gson.*;
import it.units.battleship.data.LobbyData;

import java.lang.reflect.Type;

/**
 * Serializes a Lobby object into its JSON representation using LobbyData.
 */
public class LobbySerializer implements JsonSerializer<Lobby> {

    @Override
    public JsonElement serialize(Lobby lobby, Type type, JsonSerializationContext jsonSerializationContext) {
        LobbyData lobbyData = LobbyData.builder()
                .lobbyID(lobby.getLobbyID())
                .lobbyName(lobby.getLobbyName())
                .playerOne(lobby.getPlayerOne())
                .playerTwo(lobby.getPlayerTwo())
                .build();
        return new Gson().toJsonTree(lobbyData).getAsJsonObject();
    }
}
