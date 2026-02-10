package it.units.battleship.routes.lobbies;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.javalin.websocket.*;
import it.units.battleship.GameState;
import it.units.battleship.Logger;
import it.units.battleship.WebServerApp;
import it.units.battleship.data.socket.WebSocketAuthenticationRequest;
import it.units.battleship.data.socket.WebSocketMessage;
import it.units.battleship.data.socket.payloads.GameStatusDTO;
import it.units.battleship.impl.WebSocketConnection;
import it.units.battleship.models.Lobby;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class LobbySocketClient implements WebSocketConnection {
    static HashMap<String, Lobby> connectedLobbies = new HashMap<>();
    final WebServerApp app;
    final WsContext ctx;
    final AtomicBoolean isAuthenticated = new AtomicBoolean(false);

    Lobby lobby;
    LobbiesService.PlayerType playerType;

    public LobbySocketClient(WebServerApp app, WsContext ctx) {
        Logger.log("[LobbySocketClient] New WebSocket connection initialized: " + ctx.sessionId());
        this.app = app;
        this.ctx = ctx;
    }

    /**
     * Sends the current lobby data to the client.
     * This method is called whenever there is a change in the lobby state (e.g., a player joins, a player is ready) to keep the client updated with the latest lobby information.
     */
    void updateLobbyData() {
        if (lobby != null && isAuthenticated.get()) {
            ctx.send(app.getGson().toJson(new WebSocketMessage<Lobby>("lobby", lobby)));
        }
    }

    /**
     * Handles the WebSocket connection establishment. Logs the new connection and its session ID.
     *
     * @param ctx the WebSocket connection context
     */
    @Override
    public void onConnect(WsConnectContext ctx) {
        Logger.log("[LobbySocketClient] WebSocket connection established: " + ctx.sessionId());
    }

    /**
     * Sends a message to the client through the WebSocket context.
     *
     * @param message the message to send to the client
     */
    public void send(String message) {
        if (ctx != null) {
            ctx.send(message);
        }
    }

    /**
     * Authenticates the client by verifying the provided lobby ID and player name.
     *
     * @param authMessage the authentication message containing the lobby ID and player name
     * @throws AssertionError if the client is already authenticated or if the authentication data is invalid (e.g., lobby not found, lobby full)
     */
    void authenticate(WebSocketMessage<WebSocketAuthenticationRequest> authMessage) throws AssertionError {
        if (isAuthenticated.get() || authMessage.getData() == null) {
            ctx.send(app.getGson().toJson(new WebSocketMessage<Boolean>("authenticate", false)));
            return;
        }
        Lobby lobby = app.getLobbiesService().getLobbyByID(authMessage.getData().getId());
        if (lobby == null) {
            if ("local-lobby".equals(authMessage.getData().getId())) {
                Logger.log("[SERVER DEBUG] Auto-creating local-lobby for testing.");
                lobby = new Lobby();
                lobby.setLobbyID("local-lobby");
                lobby.setLobbyName("Local Testing Lobby");
                app.getLobbiesService().addLobby(lobby);
            } else {
                ctx.send(app.getGson().toJson(new WebSocketMessage<Boolean>("authenticate", false)));
                ctx.send(app.getGson().toJson(new WebSocketMessage<String>("error", "Lobby not found")));
                return;
            }
        }
        this.lobby = lobby;
        LobbiesService.PlayerType type = app.getLobbiesService().connectPlayer(lobby.getLobbyID(), this, authMessage.getData().getName());
        if (type == LobbiesService.PlayerType.INVALID) {
            ctx.send(app.getGson().toJson(new WebSocketMessage<Boolean>("authenticate", false)));
            ctx.send(app.getGson().toJson(new WebSocketMessage<String>("error", "Lobby is full")));
            return;
        }
        this.playerType = type;
        this.isAuthenticated.set(true);
        connectedLobbies.put(lobby.getLobbyID(), lobby);
        ctx.send(app.getGson().toJson(new WebSocketMessage<Boolean>("authenticate", true)));
        updateLobbyData();
    }

    /**
     * Forwards a message to the opponent player in the lobby.
     * It checks if the client is authenticated and if the lobby is valid, then sends the message to the other player's WebSocket context.
     *
     * @param message the message to forward to the opponent
     */
    void forwardMessage(String message) {
        if (lobby != null && isAuthenticated.get()) {
            if (playerType.equals(LobbiesService.PlayerType.PLAYER_ONE) && lobby.getPlayerTwoCtx() != null) {
                lobby.getPlayerTwoCtx().send(message);
            } else if (playerType.equals(LobbiesService.PlayerType.PLAYER_TWO) && lobby.getPlayerOneCtx() != null) {
                lobby.getPlayerOneCtx().send(message);
            }
        }
    }

    /**
     * Handles incoming text messages.
     * It first checks if the message is a valid JSON with a "type" field.
     * If the client is not authenticated, it only processes "authenticate" messages.
     * If the client is authenticated, it processes messages based on their type (e.g., "lobby", "turn_change") and forwards them to the opponent if necessary.
     *
     * @param ctx the WebSocket message context
     */
    @Override
    public void onMessage(WsMessageContext ctx) {
        String message = ctx.message();
        Logger.log("[SERVER DEBUG] Raw message received: " + message);

        JsonObject fromJson = app.getGson().fromJson(message, JsonObject.class);
        if (fromJson == null || !fromJson.has("type")) {
            Logger.log("[SERVER DEBUG] Message has no type: " + message);
            return;
        }

        String messageType = fromJson.get("type").getAsString();

        if (!isAuthenticated.get()) {
            if (messageType.equals("authenticate")) {
                authenticate(app.getGson().fromJson(ctx.message(), new TypeToken<WebSocketMessage<WebSocketAuthenticationRequest>>() {
                }.getType()));
            } else {
                Logger.log("[SERVER DEBUG] Ignoring message from unauthenticated client (Type: " + messageType + ")");
            }
        } else {
            switch (messageType) {
                case "lobby" -> updateLobbyData();
                case "turn_change" -> {
                    WebSocketMessage<GameStatusDTO> statusMsg = app.getGson().fromJson(
                            ctx.message(),
                            new TypeToken<WebSocketMessage<GameStatusDTO>>() {
                            }.getType()
                    );

                    GameStatusDTO data = statusMsg.getData();

                    if (data != null && data.state() == GameState.WAITING_SETUP) {
                        if (playerType == LobbiesService.PlayerType.PLAYER_ONE) {
                            lobby.setPlayerOneReady(true);
                            Logger.log("Player One is ready");
                        } else if (playerType == LobbiesService.PlayerType.PLAYER_TWO) {
                            lobby.setPlayerTwoReady(true);
                            Logger.log("Player Two is ready");
                        }

                        if (lobby.areBothReady()) {
                            Logger.log("Both players ready! Starting game...");

                            GameStatusDTO activeTurn = new GameStatusDTO(GameState.ACTIVE_TURN, null);
                            WebSocketMessage<GameStatusDTO> msgPlayerOne = new WebSocketMessage<>("turn_change", activeTurn);
                            String responsePlayerOne = app.getGson().toJson(msgPlayerOne);

                            GameStatusDTO waitingTurn = new GameStatusDTO(GameState.WAITING_FOR_OPPONENT, null);
                            WebSocketMessage<GameStatusDTO> msgPlayerTwo = new WebSocketMessage<>("turn_change", waitingTurn);
                            String responsePlayerTwo = app.getGson().toJson(msgPlayerTwo);

                            lobby.getPlayerOneCtx().send(responsePlayerOne);
                            lobby.getPlayerTwoCtx().send(responsePlayerTwo);
                        }
                    } else {
                        forwardMessage(message);
                    }
                }
                default -> forwardMessage(message);
            }
        }
    }

    /**
     * Handles incoming binary messages.
     *
     * @param ctx the WebSocket binary message context
     */
    @Override
    public void onBinaryMessage(WsBinaryMessageContext ctx) {
    }

    /**
     * Handles WebSocket connection closure. If the client was authenticated and part of a lobby, it updates the lobby state accordingly and removes the lobby if necessary.
     *
     * @param ctx the WebSocket close context
     */
    @Override
    public void onClose(WsCloseContext ctx) {
        if (lobby != null && isAuthenticated.get()) {
            if (playerType.equals(LobbiesService.PlayerType.PLAYER_ONE)) {
                lobby.setPlayerOneCtx(null);
                lobby.setPlayerOneReady(false);
                lobby.setPlayerOne(null);
            } else if (playerType.equals(LobbiesService.PlayerType.PLAYER_TWO)) {
                lobby.setPlayerTwoCtx(null);
                lobby.setPlayerTwoReady(false);
                lobby.setPlayerTwo(null);
            }
            app.getLobbiesService().removeLobby(lobby.getLobbyID());
        }
    }

    /**
     * Handles WebSocket errors
     *
     * @param ctx the WebSocket error context
     */
    @Override
    public void onError(WsErrorContext ctx) {
        Logger.error("[LobbySocketClient] WebSocket error: " + ctx.error().getMessage());
    }
}
