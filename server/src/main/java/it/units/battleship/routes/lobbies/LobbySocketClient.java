package it.units.battleship.routes.lobbies;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.javalin.websocket.*;
import it.units.battleship.Logger;
import it.units.battleship.WebServerApp;
import it.units.battleship.data.socket.WebSocketAuthenticationRequest;
import it.units.battleship.data.socket.WebSocketMessage;
import it.units.battleship.impl.WebSocketConnection;
import it.units.battleship.models.Lobby;

import java.util.concurrent.atomic.AtomicBoolean;

public class LobbySocketClient implements WebSocketConnection {
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

    void updateLobbyData(){
        if (lobby != null && isAuthenticated.get()){
            ctx.send(app.getGson().toJson(new WebSocketMessage<Lobby>("lobby", lobby)));
        }
    }

    @Override
    public void onConnect(WsConnectContext ctx) {

    }

    public void send(String message){
        if (ctx != null){
            ctx.send(message);
        }
    }

    void authenticate(WebSocketMessage<WebSocketAuthenticationRequest> authMessage) throws AssertionError {
        if (isAuthenticated.get() || authMessage.getData() == null){
            ctx.send(app.getGson().toJson(new WebSocketMessage<Boolean>("authenticate", false)));
            return;
        }
        Lobby lobby = app.getLobbiesService().getLobbyByID(authMessage.getData().getId());
        if (lobby == null) {
            ctx.send(app.getGson().toJson(new WebSocketMessage<Boolean>("authenticate", false)));
            ctx.send(app.getGson().toJson(new WebSocketMessage<String>("error", "Lobby not found")) );
            return;
        }
        this.lobby = lobby;
        LobbiesService.PlayerType type = app.getLobbiesService().connectPlayer(lobby.getLobbyID(), this, authMessage.getData().getName());
        if (type == LobbiesService.PlayerType.INVALID){
            ctx.send(app.getGson().toJson(new WebSocketMessage<Boolean>("authenticate", false)));
            ctx.send(app.getGson().toJson(new WebSocketMessage<String>("error", "Lobby is full")) );
            return;
        }
        this.playerType = type;
        this.isAuthenticated.set(true);
        ctx.send(app.getGson().toJson(new WebSocketMessage<Boolean>("authenticate", true)));
        updateLobbyData();
    }

    void forwardMessage(String message){
        if (lobby != null && isAuthenticated.get()){
            if (playerType.equals(LobbiesService.PlayerType.PLAYER_ONE) && lobby.getPlayerTwoCtx() != null) {
                lobby.getPlayerTwoCtx().send(message);
            } else if (playerType.equals(LobbiesService.PlayerType.PLAYER_TWO) && lobby.getPlayerOneCtx() != null) {
                lobby.getPlayerOneCtx().send(message);
            }
        }
    }

    @Override
    public void onMessage(WsMessageContext ctx) {
        String message = ctx.message();
        JsonObject fromJson = app.getGson().fromJson(message, JsonObject.class);
        if (!isAuthenticated.get()){
            if (fromJson.get("type").getAsString().equals("authenticate")){
                authenticate(app.getGson().fromJson(ctx.message(), new TypeToken<WebSocketMessage<WebSocketAuthenticationRequest>>(){}.getType()));
            }
        }else{
            switch (fromJson.get("type").getAsString()){
                case "lobby" -> updateLobbyData();
                default -> forwardMessage(message);
            }
        }
    }

    @Override
    public void onBinaryMessage(WsBinaryMessageContext ctx) {

    }

    @Override
    public void onClose(WsCloseContext ctx) {
        if (lobby != null && isAuthenticated.get()){

        }
    }

    @Override
    public void onError(WsErrorContext ctx) {

    }
}
