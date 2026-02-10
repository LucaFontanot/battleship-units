package it.units.battleship.impl;

import io.javalin.websocket.*;

public interface WebSocketConnection {

    /**
     * Handles the WebSocket connection establishment. This method is called when a new WebSocket connection is established between the client and the server. It receives a WsConnectContext object that contains information about the connection, such as the client's IP address and the WebSocket context in which the connection was established. Implementing this method allows you to perform any necessary setup or initialization when a new WebSocket connection is made.
     *
     * @param ctx the WebSocket connection context containing information about the connection
     */
    void onConnect(WsConnectContext ctx);

    /**
     * Handles incoming text messages. This method is called when a text message is received from the client. It receives a WsMessageContext object that contains the message data and the WebSocket context in which the message was received. Implementing this method allows you to process text messages sent by the client, such as chat messages or commands.
     *
     * @param ctx the WebSocket message context containing the message data and the WebSocket context in which the message was received
     */
    void onMessage(WsMessageContext ctx);

    /**
     * Handles incoming binary messages. This method is called when a binary message is received from the client. It receives a WsBinaryMessageContext object that contains the binary data and the WebSocket context in which the message was received. Implementing this method allows you to process binary messages sent by the client, such as file uploads or other types of binary data.
     *
     * @param ctx the WebSocket binary message context containing the binary data and the WebSocket context in which the message was received
     */
    void onBinaryMessage(WsBinaryMessageContext ctx);

    /**
     * Handles the WebSocket connection closure. This method is called when a WebSocket connection is closed, either by the client or the server. It receives a WsCloseContext object that contains information about the closure, such as the reason for closure and the WebSocket context in which the closure occurred. Implementing this method allows you to perform any necessary cleanup or logging when a WebSocket connection is closed.
     *
     * @param ctx the WebSocket close context containing information about the closure
     */
    void onClose(WsCloseContext ctx);

    /**
     * Handles WebSocket errors. This method is called when an error occurs during the WebSocket communication. It receives a WsErrorContext object that contains information about the error, such as the exception that was thrown and the WebSocket context in which the error occurred. Implementing this method allows you to log errors, clean up resources, or take other appropriate actions when a WebSocket error happens.
     *
     * @param ctx the WebSocket error context containing information about the error
     */
    void onError(WsErrorContext ctx);
}
