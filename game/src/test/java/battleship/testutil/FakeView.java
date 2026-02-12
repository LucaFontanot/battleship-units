package battleship.testutil;

import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import it.units.battleship.controller.game.actions.GridInteractionObserver;
import it.units.battleship.model.Ship;
import it.units.battleship.view.core.BattleshipView;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fake implementation of BattleshipView for testing purposes.
 * Records all interactions so tests can assert on them.
 */
public class FakeView implements BattleshipView {
    public boolean opened;
    public boolean disposed;
    public GridInteractionObserver playerGridListener;
    public GridInteractionObserver opponentGridListener;
    public boolean playerTurn;
    public String lastSystemMessage;
    public String lastEndGameMessage;
    public Coordinate lastShotPreview;
    public int errorSoundCount;
    public boolean transitionToGamePhaseCalled;
    public String lastWaitingMessage;
    public String lastPlayerGridSerialized;
    public List<Ship> lastPlayerFleet;
    public String lastOpponentGridSerialized;
    public List<Ship> lastOpponentFleet;
    public Set<Coordinate> lastPlacementCoords;
    public Boolean lastPlacementValid;
    public Ship lastPreviewShip;
    public Map<ShipType, Integer> lastPlacedCounts;
    public Map<ShipType, Integer> lastRequiredCounts;
    public Runnable returnToMenuAction;
    public boolean returnToMenuVisible;

    public ShipType selectedShipType = ShipType.DESTROYER;
    public Orientation selectedOrientation = Orientation.HORIZONTAL_RIGHT;

    @Override
    public void open() { opened = true; }
    @Override
    public void dispose() { disposed = true; }
    @Override
    public void setPlayerGridListener(GridInteractionObserver observer) { playerGridListener = observer; }
    @Override
    public void setOpponentGridListener(GridInteractionObserver observer) { opponentGridListener = observer; }
    @Override
    public ShipType getSelectedShipType() { return selectedShipType; }
    @Override
    public Orientation getSelectedOrientation() { return selectedOrientation; }
    @Override
    public void showPlacementPreview(Set<Coordinate> coordinates, boolean valid, Ship previewShip) {
        lastPlacementCoords = coordinates;
        lastPlacementValid = valid;
        lastPreviewShip = previewShip;
    }
    @Override
    public void refreshFleetSelection(Map<ShipType, Integer> placedCounts, Map<ShipType, Integer> requiredCounts) {
        lastPlacedCounts = placedCounts;
        lastRequiredCounts = requiredCounts;
    }
    @Override
    public void updatePlayerGrid(String gridSerialized, List<Ship> fleet) {
        lastPlayerGridSerialized = gridSerialized;
        lastPlayerFleet = fleet;
    }
    @Override
    public void updateOpponentGrid(String gridSerialized, List<Ship> fleet) {
        lastOpponentGridSerialized = gridSerialized;
        lastOpponentFleet = fleet;
    }
    @Override
    public void setPlayerTurn(boolean isPlayerTurn) { playerTurn = isPlayerTurn; }
    @Override
    public void showShotPreview(Coordinate coordinate) { lastShotPreview = coordinate; }
    @Override
    public void showEndGamePhase(String message) { lastEndGameMessage = message; }
    @Override
    public void showSystemMessage(String message) { lastSystemMessage = message; }
    @Override
    public void setReturnToMenuAction(Runnable action) { returnToMenuAction = action; }
    @Override
    public void setReturnToMenuVisible(boolean visible) { returnToMenuVisible = visible; }
    @Override
    public void playerErrorSound() { errorSoundCount++; }
    @Override
    public void transitionToGamePhase() { transitionToGamePhaseCalled = true; }
    @Override
    public void showWaitingForOpponent(String message) { lastWaitingMessage = message; }
}
