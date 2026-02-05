package battleship.controller.handlers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.units.battleship.Logger;
import it.units.battleship.data.socket.WebSocketMessage;
import it.units.battleship.data.socket.payloads.GameConfigDTO;
import it.units.battleship.data.socket.payloads.GameStatusDTO;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Handles network communication with the battleship server using WebSockets.
 * It manages connection lifecycle and serializes/deserializes game messages.
 */
import it.units.battleship.data.socket.GameMessageType;

@Slf4j
public class NetworkClient extends AbstractPlayerCommunication{

    private final WebSocketClient client;
    private final Gson gson = new Gson();

    public NetworkClient(String serverUri) throws URISyntaxException {
        this.client = new WebSocketClient(new URI(serverUri)) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Logger.log("Server connected: " + getURI());
            }

            @Override
            public void onMessage(String message) {
                Logger.log("Message received: " + message);
                handleIncomingMessage(message);
            }

            @Override
            public void onClose(int i, String reason, boolean b) {
                Logger.log("Connection closed: " + reason);
            }

            @Override
            public void onError(Exception e) {
                Logger.log("Websocket error: " + e.getMessage());
            }
        };
    }

    public void connect() throws InterruptedException {
        client.connectBlocking();
    }

    @Override
    public <T> void sendMessage(GameMessageType type, T payload) {
        if (client != null && client.isOpen()){
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
