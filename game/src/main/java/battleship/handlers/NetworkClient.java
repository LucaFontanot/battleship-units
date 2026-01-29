package battleship.handlers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.units.battleship.Logger;
import it.units.battleship.data.socket.WebSocketMessage;
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
    public <T> void sendMessage(String type, T payload) {
        if (client != null && client.isOpen()){
            WebSocketMessage<T> message = new WebSocketMessage<>(type, payload);
            String json = gson.toJson(message);
            client.send(json);
        }else {
            Logger.warn("Failed send message: WebSocket not connected.");
        }
    }

    private void handleIncomingMessage(String json){
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        String type = jsonObject.get("type").getAsString();

        switch (type){
            case "grid_update" -> {
                WebSocketMessage<GridUpdateDTO> msg = gson.fromJson(
                        json,
                        new TypeToken<WebSocketMessage<GridUpdateDTO>>(){}.getType()
                );

                this.communicationEventsListeners.forEach(l -> l.onOpponentGridUpdate(msg.getData()));
            }
            case "shot_request" -> {
                WebSocketMessage<ShotRequestDTO> msg = gson.fromJson(
                        json,
                        new TypeToken<WebSocketMessage<ShotRequestDTO>>(){}.getType()
                );

                this.communicationEventsListeners.forEach(l -> l.onShotReceived(msg.getData()));
            }
        }

    }
}
