package battleship.view.core;

import battleship.controller.game.actions.GridInteractionObserver;
import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Unified view interface that supports both setup and game phases.
 * Implementations can be a single frame that changes panels, or separate frames.
 */
public interface BattleshipView {

    // ===== Lifecycle =====
    void open();
    void dispose();

    // ===== Grid Listeners =====
    void setPlayerGridListener(GridInteractionObserver observer);
    void setOpponentGridListener(GridInteractionObserver observer);

    // ===== Setup Phase Methods =====
    ShipType getSelectedShipType();
    Orientation getSelectedOrientation();
    void showPlacementPreview(Set<Coordinate> coordinates, boolean valid, Ship previewShip);
    void refreshFleetSelection(Map<ShipType, Integer> placedCounts, Map<ShipType, Integer> requiredCounts);
    void updatePlayerGrid(String gridSerialized, List<Ship> fleet);

    // ===== Game Phase Methods =====
    void updateOpponentGrid(String gridSerialized, List<Ship> fleet);
    void setPlayerTurn(boolean isPlayerTurn);
    void showShotPreview(Coordinate coordinate);
    void showEndGamePhase(String message);
    void showSystemMessage(String message);
    void setReturnToMenuAction(Runnable action);
    void setReturnToMenuVisible(boolean visible);

    // ===== Audio Feedback =====
    void playerErrorSound();

    // ===== Phase Transitions =====
    /**
     * Transitions the view from setup phase to game phase.
     * This might swap panels, open a new window, etc.
     */
    void transitionToGamePhase();

    /**
     * Shows a waiting screen during setup synchronization.
     */
    void showWaitingForOpponent(String message);
}