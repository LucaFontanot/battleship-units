package it.units.battleship.controller.mode;

import it.units.battleship.controller.mode.ai.AIOpponent;
import it.units.battleship.controller.mode.ai.SimpleAIOpponent;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static it.units.battleship.Defaults.*;

/**
 * Represents the single-player strategy for the game.
 * Handles interactions between the player and an AI opponent.
 */
public class SinglePlayerStrategy implements GameModeStrategy {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Map<ShipType, Integer> requiredFleetConfiguration;
    private GameModeCallback callback;
    private AIOpponent aiOpponent;
    private Grid aiGrid;
    private FleetManager aiFleetManager;

    public SinglePlayerStrategy(Map<ShipType, Integer> requiredFleetConfiguration) {
        this.requiredFleetConfiguration = requiredFleetConfiguration;
    }

    @Override
    public void initialize(GameModeCallback callback) {
        this.callback = callback;

        this.aiGrid = new Grid(GRID_ROWS, GRID_COLS);
        this.aiFleetManager = new FleetManager(aiGrid, requiredFleetConfiguration);
        this.aiOpponent = new SimpleAIOpponent(aiGrid, aiFleetManager);

        aiOpponent.placeShips();

        Logger.log("SinglePlayerStrategy initialized");

        callback.onOpponentReady();
    }

    @Override
    public void sendShot(Coordinate coordinate) {
        executor.submit(() -> {
            simulatedDelay(DELAY_MS);
            processPlayerShot(coordinate);

            if (aiFleetManager.isGameOver()) {
                callback.onGameStatusReceived(GameState.GAME_OVER, "You win! All enemy ships destroyed!");
                return;
            }

            simulatedDelay(DELAY_MS);
            Coordinate aiShot = aiOpponent.calculateNextShot();
            callback.onShotReceived(aiShot);
        });
    }

    private void processPlayerShot(Coordinate coordinate){
        boolean hit = aiFleetManager.handleIncomingShot(coordinate);

        String gridSerialized = GridMapper.serialize(aiGrid.getGrid());
        List<Ship> aiFleet = aiFleetManager.getFleet().stream().filter(Ship::isSunk).collect(Collectors.toUnmodifiableList());

        callback.onGridUpdateReceived(gridSerialized, aiFleet);
    }

    private void simulatedDelay(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void sendGridUpdate(Grid grid, List<Ship> fleet, boolean shotOutcome) {
        aiOpponent.processLastShotResult(grid, fleet, shotOutcome);
    }

    @Override
    public void sendGameOver(String message) {
        Logger.log("SinglePlayerStrategy: Game over - " + message);
    }

    @Override
    public void notifySetupComplete() {
        Logger.log("SinglePlayerStrategy: Player setup complete, starting game");
        callback.onGameStatusReceived(GameState.ACTIVE_TURN, "Ready to play");
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public String getModeName() {
        return "Single Player";
    }
}
