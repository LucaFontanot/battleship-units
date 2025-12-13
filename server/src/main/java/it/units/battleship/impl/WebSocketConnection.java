package it.units.battleship.impl;

import io.javalin.websocket.*;

public interface WebSocketConnection {

    void onConnect(WsConnectContext ctx);

    void onMessage(WsMessageContext ctx);

    void onBinaryMessage(WsBinaryMessageContext ctx);

    void onClose(WsCloseContext ctx);

    void onError(WsErrorContext ctx);
}
