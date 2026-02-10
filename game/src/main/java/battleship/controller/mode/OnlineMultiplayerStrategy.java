package battleship.controller.mode;

import battleship.controller.game.network.NetworkClient;
import battleship.controller.game.network.NetworkEventsHandler;
import battleship.controller.game.actions.NetworkInputActions;
import battleship.model.Grid;
import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Logger;

import java.util.List;

/**
 * For online multiplayer gameplay.
 * This mode enable communication with an opponent over a network
 */
public class OnlineMultiplayerStrategy implements GameModeStrategy {

    private final NetworkClient networkClient;

    public OnlineMultiplayerStrategy(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    @Override
    public void initialize(GameModeCallback callback) {
        NetworkInputActions inputActions = new OnlineMultiplayerInputHandler(callback);
        networkClient.addCommunicationEventsListener(new NetworkEventsHandler(inputActions));

        Logger.log("OnlineMultiplayerStrategy initialized");
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
        Logger.log("OnlineMultiplayerStrategy: Shutting down");
    }

    @Override
    public String getModeName() {
        return "Online Multiplayer";
    }

    private static class OnlineMultiplayerInputHandler implements NetworkInputActions {
        private final GameModeCallback callback;

        OnlineMultiplayerInputHandler(GameModeCallback callback) {
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
