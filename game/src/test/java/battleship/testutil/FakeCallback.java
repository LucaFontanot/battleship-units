package battleship.testutil;

import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.controller.mode.GameModeStrategy;
import it.units.battleship.model.Ship;

import java.util.List;

/**
 * Fake implementation of GameModeCallback for testing purposes.
 * Records all callback invocations so tests can assert on them.
 */
public class FakeCallback implements GameModeStrategy.GameModeCallback {
    public boolean opponentReadyCalled;
    public Coordinate lastShotReceived;
    public String lastGridUpdateSerialized;
    public List<Ship> lastGridUpdateFleet;
    public GameState lastGameStatusState;
    public String lastGameStatusMessage;
    public String lastConnectionError;

    @Override
    public void onShotReceived(Coordinate coordinate) { lastShotReceived = coordinate; }

    @Override
    public void onGridUpdateReceived(String gridSerialized, List<Ship> fleet) {
        lastGridUpdateSerialized = gridSerialized;
        lastGridUpdateFleet = fleet;
    }

    @Override
    public void onGameStatusReceived(GameState state, String message) {
        lastGameStatusState = state;
        lastGameStatusMessage = message;
    }

    @Override
    public void onConnectionError(String error) { lastConnectionError = error; }
}
