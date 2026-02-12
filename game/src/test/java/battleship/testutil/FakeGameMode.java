package battleship.testutil;

import it.units.battleship.Coordinate;
import it.units.battleship.controller.mode.GameModeStrategy;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;

import java.util.List;

/**
 * Fake implementation of GameModeStrategy for testing purposes.
 * Records all calls so tests can assert on them.
 */
public class FakeGameMode implements GameModeStrategy {
    public GameModeCallback lastCallback;
    public Coordinate lastShotSent;
    public String lastGameOverMessage;
    public boolean setupCompleteNotified;
    public boolean shutdownCalled;
    public Grid lastSentGrid;
    public List<Ship> lastSentFleet;
    public Boolean lastSentShotOutcome;

    @Override
    public void initialize(GameModeCallback callback) { this.lastCallback = callback; }
    @Override
    public void sendShot(Coordinate coordinate) { this.lastShotSent = coordinate; }
    @Override
    public void sendGridUpdate(Grid grid, List<Ship> fleet, boolean shotOutcome) {
        this.lastSentGrid = grid;
        this.lastSentFleet = fleet;
        this.lastSentShotOutcome = shotOutcome;
    }
    @Override
    public void sendGameOver(String message) { this.lastGameOverMessage = message; }
    @Override
    public void notifySetupComplete() { this.setupCompleteNotified = true; }
    @Override
    public void shutdown() { this.shutdownCalled = true; }
    @Override
    public String getModeName() { return "Test Mode"; }
}
