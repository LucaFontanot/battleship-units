package battleship.controller.mode;

import battleship.controller.game.actions.NetworkInputActions;
import battleship.controller.game.network.NetworkEventsHandler;
import battleship.controller.game.network.NetworkClient;
import battleship.model.game.Grid;
import battleship.model.game.Ship;
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
            // Updated to use the new NetworkClient constructor that takes URI
            networkClient = new NetworkClient(new java.net.URI(serverUri), isHost ? "Host" : "Guest");

            NetworkInputActions inputActions = new LocalMultiplayerInputHandler(callback);

            networkClient.addCommunicationEventsListener(new NetworkEventsHandler(inputActions));

            // Connection is handled asynchronously in the constructor for the new NetworkClient
            Logger.log("LocalMultiplayerStrategy: Connecting to " + serverUri);
            
        } catch (java.net.URISyntaxException e) {
            Logger.error("LocalMultiplayerStrategy: Connection failed - " + e.getMessage());
            callback.onConnectionError("Failed to connect: " + e.getMessage());
        }
    }

    @Override
    public void sendShot(Coordinate coordinate) {
        networkClient.sendShotRequest(coordinate);
    }

    @Override
    public void sendGridUpdate(Grid grid, List<Ship> fleet, boolean shotOutcome) {
        networkClient.sendGridUpdate(grid, fleet, shotOutcome);
    }

    @Override
    public void sendGameOver(String message) {
        networkClient.sendGameStatus(GameState.GAME_OVER, message);
    }

    @Override
    public void notifySetupComplete() {
        networkClient.sendGameStatus(GameState.WAITING_SETUP, "Ready to play");
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
