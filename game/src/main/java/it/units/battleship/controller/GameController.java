package it.units.battleship.controller;

import it.units.battleship.controller.game.ui.OpponentGridHandler;
import it.units.battleship.controller.game.ui.PlayerGridHandler;
import it.units.battleship.controller.mode.GameModeStrategy;
import it.units.battleship.controller.turn.TurnManager;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Ship;
import it.units.battleship.view.core.BattleshipView;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Logger;
import lombok.NonNull;

import java.util.List;


/**
 * Main glue class that keeps the model, view and networking layer talking.
 * This class ended up a bit bigger than planned, but for now it keeps things readable.
 */
public class GameController implements GameModeStrategy.GameModeCallback {

    private final TurnManager turnManager;
    private final GameModeStrategy gameMode;
    private final BattleshipView view;

    public GameController(@NonNull FleetManager fleetManager,
                          @NonNull GameModeStrategy gameMode,
                          @NonNull BattleshipView view) {
        this.gameMode = gameMode;
        this.view = view;

        this.turnManager = new TurnManager(fleetManager, view, gameMode);

        view.setOpponentGridListener(new OpponentGridHandler(turnManager));
        view.setPlayerGridListener(new PlayerGridHandler(turnManager));

        turnManager.setSetupCompleteCallback(this::onLocalSetupComplete);

        Logger.log("GameController: Initialized using mode -> " + gameMode.getModeName());
    }

    /**
     * It starts the game by initializing the game mode and starting the TurnManager.
     */
    public void startGame() {
        view.open();
        gameMode.initialize(this);
        turnManager.start();
    }

    /**
     * Called when the game setup is complete for the local player.
     */
    private void onLocalSetupComplete() {
        Logger.log("GameController: Local setup complete");

        turnManager.transitionToWaitingSetup();

        gameMode.notifySetupComplete();
    }

    // ===== GameModeCallback implementation =====

    @Override
    public void onOpponentReady() {
        Logger.log("GameController: Opponent ready");
    }

    @Override
    public void onShotReceived(Coordinate coordinate) {
        Logger.log("GameController: Shot received at " + coordinate);
        turnManager.handleIncomingShot(coordinate);
    }

    @Override
    public void onGridUpdateReceived(String gridSerialized, List<Ship> fleet) {
        Logger.log("GameController: grid update received");
        turnManager.handleOpponentGridUpdate(gridSerialized, fleet);
    }

    @Override
    public void onGameStatusReceived(GameState state, String message) {
        Logger.log("GameController: game status -> " + state + " | " + message);

        if (state == GameState.GAME_OVER) {
            turnManager.handleGameOver(message);
        } else if (state == GameState.ACTIVE_TURN || state == GameState.WAITING_FOR_OPPONENT) {
            turnManager.handleGameStatusReceived(state);
        }
    }

    @Override
    public void onConnectionError(String error) {
        Logger.error("GameController: connection error -> " + error);
        view.showEndGamePhase("Connection error: " + error);
    }
}