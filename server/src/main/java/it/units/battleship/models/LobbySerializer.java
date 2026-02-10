package it.units.battleship.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import it.units.battleship.data.LobbyData;

import java.lang.reflect.Type;

/**
 * Serializes a Lobby object into its JSON representation using LobbyData.
 */
public class LobbySerializer implements JsonSerializer<Lobby> {

    /**
     * Serializes a Lobby object into JSON format. It creates a LobbyData object from the Lobby instance and then converts it to a JSON element using Gson.
     *
     * @param lobby                    the Lobby object to serialize
     * @param type                     the type of the object being serialized
     * @param jsonSerializationContext the context for serialization, not used in this implementation
     * @return a JsonElement representing the serialized Lobby object
     */
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
