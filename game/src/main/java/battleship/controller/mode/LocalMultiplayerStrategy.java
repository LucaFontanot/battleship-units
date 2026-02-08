package battleship.controller.mode;

import battleship.controller.actions.NetworkInputActions;
import battleship.controller.handlers.network.NetworkInputHandler;
import battleship.controller.handlers.network.NetworkOutputHandler;
import battleship.controller.network.NetworkClient;
import battleship.model.Grid;
import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Logger;
import lombok.Getter;

import java.net.URISyntaxException;
import java.util.List;

public class LocalMultiplayerStrategy implements GameModeStrategy{
    private final String serverUri;
    @Getter
    private final boolean isHost;

    private GameModeCallback callback;
    private NetworkClient networkClient;
    private NetworkOutputHandler outputHandler;
    private NetworkInputHandler inputHandler;

    /**
     * @param serverUri URI del WebSocket server (es. "ws://localhost:8080/game")
     * @param isHost true se questo giocatore è l'host (inizia per primo)
     */
    public LocalMultiplayerStrategy(String serverUri, boolean isHost) {
        this.serverUri = serverUri;
        this.isHost = isHost;
    }

    @Override
    public void initialize(GameModeCallback callback) {
        this.callback = callback;

        try {
            networkClient = new NetworkClient(serverUri);

            NetworkInputActions inputActions = new LocalMultiplayerInputHandler(callback);

            outputHandler = new NetworkOutputHandler(networkClient);

            networkClient.addCommunicationEventsListener(new NetworkInputHandler(inputActions));

            boolean connected = networkClient.connect();

            if (connected){
                Logger.log("LocalMultiplayerStrategy: Successfully connected to " + serverUri);
                // Temporary authentication for local multiplayer testing
                it.units.battleship.data.socket.WebSocketAuthenticationRequest auth = 
                    new it.units.battleship.data.socket.WebSocketAuthenticationRequest("local-lobby", isHost ? "Host" : "Guest");
                networkClient.sendMessage(it.units.battleship.data.socket.GameMessageType.AUTHENTICATE, auth); 
            }else {
                Logger.log("LocalMultiplayerStrategy: Connected refused to " + serverUri);
                callback.onConnectionError("Failed to connect" + serverUri);
            }
        } catch (InterruptedException | URISyntaxException e) {
            Logger.error("LocalMultiplayerStrategy: Connection failed - " + e.getMessage());
            callback.onConnectionError("Failed to connect: " + e.getMessage());
        }
    }

    @Override
    public void sendShot(Coordinate coordinate) {
        outputHandler.sendShotRequest(coordinate);
    }

    @Override
    public void sendGridUpdate(Grid grid, List<Ship> fleet, boolean shotOutcome) {
        outputHandler.sendGridUpdate(grid, fleet, shotOutcome);
    }

    @Override
    public void sendGameOver(String message) {
        outputHandler.sendGameStatus(GameState.GAME_OVER, message);
    }

    @Override
    public void notifySetupComplete() {
        outputHandler.sendGameStatus(GameState.WAITING_SETUP, "Ready to play");
    }

    @Override
    public void shutdown() {
        Logger.log("LocalMultiplayerStrategy: Shutting down");
    }

    @Override
    public String getModeName() {
        return "Local Multiplayer" + (isHost ? " (Host)" : " (Guest)");
    }

    /**
     * Internal handler to convert the network msg into callbacks.
     */
    private static class LocalMultiplayerInputHandler
            implements NetworkInputActions {

        private final GameModeCallback callback;

        LocalMultiplayerInputHandler(GameModeCallback callback) {
            this.callback = callback;
        }

        @Override
        public void processIncomingShot(Coordinate coordinate) {
            callback.onShotReceived(coordinate);
        }

        @Override
        public void processOpponentGridUpdate(String grid, List<Ship> revealedFleet) {
            callback.onGridUpdateReceived(grid, revealedFleet);
        }

        @Override
        public void processGameStatusUpdate(GameState newState, String message) {
            callback.onGameStatusReceived(newState, message);
        }
    }
}
