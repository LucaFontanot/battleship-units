package battleship.controller.network;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.units.battleship.Defaults;
import it.units.battleship.Logger;
import it.units.battleship.data.LobbyData;
import it.units.battleship.data.socket.WebSocketAuthenticationRequest;
import it.units.battleship.data.socket.WebSocketMessage;
import it.units.battleship.data.socket.payloads.GameConfigDTO;
import it.units.battleship.data.socket.payloads.GameStatusDTO;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles network communication with the battleship server using WebSockets.
 * It manages connection lifecycle and serializes/deserializes game messages.
 */
import it.units.battleship.data.socket.GameMessageType;
import okhttp3.*;
import okio.ByteString;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Slf4j
public class NetworkClient extends AbstractPlayerCommunication{

    private final WebSocket client;
    private final Gson gson = new Gson();
    @Getter
    private boolean isConnected = false;
    @Getter
    private boolean isAuthenticated = false;
    private final String playerName;
    private final LobbyData lobbyData;

    public NetworkClient(LobbyData data, String playerName) {
        Request request = new Request.Builder()
                .url(Defaults.WEBSOCKET_LOBBY_ENDPOINT)
                .build();
        this.client = new OkHttpClient().newWebSocket(request, new WebSocketListener() {
            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Logger.log("WebSocket closed: " + reason);
                isConnected = false;
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Logger.log("WebSocket closing: " + reason);
                isConnected = false;
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                Logger.error("WebSocket error: " + t.getMessage());
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Logger.log("Message received: " + text);
                handleIncomingMessage(text);
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                Logger.warn("Received unexpected binary message: " + bytes.hex());
                handleIncomingMessage(bytes.utf8());
            }

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Logger.log("WebSocket connection opened: " + webSocket.request().url());
                isConnected = true;
            }
        });
        this.playerName = playerName;
        this.lobbyData = data;
    }

    void beginAuthentication() {
        WebSocketAuthenticationRequest webSocketAuthenticationRequest = new WebSocketAuthenticationRequest(lobbyData.getLobbyID(), playerName);
        WebSocketMessage<WebSocketAuthenticationRequest> authMessage = new WebSocketMessage<>("authenticate", webSocketAuthenticationRequest);
        String authMessageJson = gson.toJson(authMessage);
        client.send(authMessageJson);
    }

    @Override
    public <T> void sendMessage(GameMessageType type, T payload) {
        if (client != null && isConnected()){
            WebSocketMessage<T> message = new WebSocketMessage<>(type.getType(), payload);
            String json = gson.toJson(message);
            client.send(json);
        }else {
            Logger.warn("Failed send message: WebSocket not connected.");
        }
    }

    private void handleIncomingMessage(String json){
        try {
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            if (jsonObject == null || !jsonObject.has("type")) return;

            String typeString = jsonObject.get("type").getAsString();

            if (typeString.equals("authenticate")){
                if (jsonObject.get("data").getAsBoolean()){
                    isAuthenticated = true;
                    Logger.log("Authentication successful for player: " + playerName);
                } else {
                    Logger.error("Authentication failed for player: " + playerName);
                    client.close(1000, "Authentication failed");
                }
                return;
            }

            GameMessageType type = java.util.Arrays.stream(GameMessageType.values())
                    .filter(t -> t.getType().equals(typeString))
                    .findFirst()
                    .orElse(null);

            if (type == null){
                Logger.warn("Received unknown message type: " + typeString);
                return;
            }

            dispatchMessage(type, json);
        } catch (Exception e) {
            Logger.error("Failed to parse incoming JSON: " + e.getMessage());
        }
    }

    private void dispatchMessage(GameMessageType type, String json){
        switch (type){
            case GRID_UPDATE -> {
                WebSocketMessage<GridUpdateDTO> msg = gson.fromJson(
                        json,
                        new TypeToken<WebSocketMessage<GridUpdateDTO>>(){}.getType()
                );

                this.communicationEventsListeners.forEach(l -> l.onOpponentGridUpdate(msg.getData()));
            }
            case SHOT_REQUEST -> {
                WebSocketMessage<ShotRequestDTO> msg = gson.fromJson(
                        json,
                        new TypeToken<WebSocketMessage<ShotRequestDTO>>(){}.getType()
                );

                this.communicationEventsListeners.forEach(l -> l.onShotReceived(msg.getData()));
            }
            case GAME_SETUP -> {
                WebSocketMessage<GameConfigDTO> msg = gson.fromJson(
                        json,
                        new TypeToken<WebSocketMessage<GameConfigDTO>>(){}.getType()
                );

                this.communicationEventsListeners.forEach(l -> l.onGameSetupReceived(msg.getData()));
            }
            case TURN_CHANGE -> {
                WebSocketMessage<GameStatusDTO> msg = gson.fromJson(
                        json,
                        new TypeToken<WebSocketMessage<GameStatusDTO>>(){}.getType()
                );

                this.communicationEventsListeners.forEach(l -> l.onGameStatusReceived(msg.getData()));
            }
            default -> Logger.log("Unhandled message type: " + type);
        }
    }
}
