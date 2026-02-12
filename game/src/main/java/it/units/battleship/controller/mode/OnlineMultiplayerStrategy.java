package it.units.battleship.controller.mode;

import it.units.battleship.controller.game.actions.NetworkActionsReceiver;
import it.units.battleship.controller.game.network.AbstractPlayerCommunication;
import it.units.battleship.controller.game.network.NetworkClient;
import it.units.battleship.controller.game.network.NetworkEventsHandler;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Logger;

import java.util.List;

import static it.units.battleship.Defaults.MSG_READY_TO_PLAY;

/**
 * For online multiplayer gameplay.
 * This mode enable communication with an opponent over a network
 */
public class OnlineMultiplayerStrategy implements GameModeStrategy {

    private final AbstractPlayerCommunication network;

    public OnlineMultiplayerStrategy(AbstractPlayerCommunication network) {
        this.network = network;
    }

    @Override
    public void initialize(GameModeCallback callback) {
        NetworkActionsReceiver inputActions = new OnlineMultiplayerInputHandler(callback);
        network.addCommunicationEventsListener(new NetworkEventsHandler(inputActions));

        Logger.log("OnlineMultiplayerStrategy initialized");
    }

    @Override
    public void sendShot(Coordinate coordinate) {
        network.sendShotRequest(coordinate);
    }

    @Override
    public void sendGridUpdate(Grid grid, List<Ship> fleet, boolean shotOutcome) {
        network.sendGridUpdate(grid, fleet, shotOutcome);
    }

    @Override
    public void sendGameOver(String message) {
        network.sendGameStatus(GameState.GAME_OVER, message);
    }

    @Override
    public void notifySetupComplete() {
        network.sendGameStatus(GameState.WAITING_SETUP, MSG_READY_TO_PLAY);
    }

    @Override
    public void shutdown() {
        Logger.log("OnlineMultiplayerStrategy: Shutting down");
    }

    @Override
    public String getModeName() {
        return "Online Multiplayer";
    }

    private static class OnlineMultiplayerInputHandler implements NetworkActionsReceiver {
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
