package it.units.battleship.controller.turn;

import it.units.battleship.controller.game.actions.GameInteractionFacade;
import it.units.battleship.controller.mode.GameModeStrategy;
import it.units.battleship.controller.turn.states.*;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.view.core.BattleshipView;
import it.units.battleship.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Manages the turn flow using the State pattern.
 * Context role in the pattern and coord the transactions between states.
 */
public class TurnManager implements GameInteractionFacade {
    @Getter
    private final FleetManager fleetManager;
    @Getter
    private final BattleshipView view;
    @Getter
    private final GameModeStrategy gameModeStrategy;
    @Getter
    private TurnState currentState;
    @Getter
    @Setter
    private Grid opponentGrid;
    @Setter
    private SetupCompleteCallback setupCompleteCallback;

    public TurnManager(@NonNull FleetManager fleetManager,
                       @NonNull BattleshipView view,
                       @NonNull GameModeStrategy gameModeStrategy) {
        this.fleetManager = fleetManager;
        this.gameModeStrategy = gameModeStrategy;
        this.view = view;
        //using grid.getRow and grid.getCol indeed using the default values,
        //oppoennt grid will scale automatically if diff grid size are implemented
        this.opponentGrid = new Grid(fleetManager.getGridRows(), fleetManager.getGridCols());

        this.currentState = new SetupState();
    }

    /**
     * Starts the TurnManager, entering in the initial state.
     */
    public void start() {
        currentState.onEnter(this);
    }

    /**
     * Called when the game setup is complete.
     */
    public void onSetupComplete() {
        if (setupCompleteCallback != null) {
            setupCompleteCallback.onSetupComplete();
        }
    }

    /**
     * Updates the player grid UI with the current model state.
     */
    public void refreshUI() {
        String gridSerialized = fleetManager.getSerializedGridState();
        view.updatePlayerGrid(gridSerialized, fleetManager.getFleet());
    }

    /**
     * Updates the fleet selection UI with current placement counts and requirements.
     */
    public void refreshFleetUI() {
        Map<ShipType, Integer> placedCounts = fleetManager.getPlacedCounts();
        Map<ShipType, Integer> requiredCounts = fleetManager.getRequiredFleetConfiguration();

        view.refreshFleetSelection(placedCounts, requiredCounts);
    }

    /**
     * Sets whether it is currently the player's turn.
     */
    public void setPlayerTurn(boolean isPlayerTurn) {
        view.setPlayerTurn(isPlayerTurn);
    }

    /**
     * Retrieves the state of a cell in the opponent's grid.
     */
    public CellState getOpponentCellState(Coordinate coordinate) {
        return opponentGrid.getState(coordinate);
    }

    /**
     * Notifies the user with a system message in the view.
     */
    public void notifyUser(String message) {
        view.showSystemMessage(message);
    }

    /**
     * Commands the current game mode strategy to send a shot to the opponent.
     */
    public void executeShot(Coordinate coordinate) {
        gameModeStrategy.sendShot(coordinate);
    }

    /**
     * Renders a preview of a shot on the opponent's grid.
     */
    public void renderShotPreview(Coordinate coordinate) {
        view.showShotPreview(coordinate);
    }

    /**
     * Transitions the view to the end game phase display.
     */
    public void transitionToEndGamePhase(String message) {
        view.showEndGamePhase(message);
    }

    /**
     * Transitions the view from the setup phase to the game phase.
     */
    public void transitionToGamePhase() {
        view.transitionToGamePhase();
    }

    /**
     * Notifies the game mode strategy that the game is over.
     */
    public void sendGameOverStatus(String message) {
        gameModeStrategy.sendGameOver(message);
    }

    /**
     * Updates the opponent's grid display with new data.
     */
    public void updateOpponentGrid(String grid, List<Ship> fleet) {
        view.updateOpponentGrid(grid, fleet);
    }

    /**
     * Attempts to place a ship at the specified coordinate using the current view selection.
     */
    public void tryPlaceShip(Coordinate coordinate) {
        Orientation orientation = view.getSelectedOrientation();
        ShipType shipType = view.getSelectedShipType();

        if (shipType == null) return;

        try {
            Ship ship = Ship.createShip(coordinate, orientation, shipType, fleetManager.getGrid());
            boolean placed = fleetManager.addShip(ship);

            if (placed) {
                refreshUI();
                refreshFleetUI();

                if (fleetManager.isFleetComplete()) {
                    onSetupComplete();
                }
            } else {
                view.playerErrorSound();
                view.showPlacementPreview(ship.getCoordinates(), false, ship);
            }
        } catch (IllegalArgumentException ex) {
            view.playerErrorSound();
            LinkedHashSet<Coordinate> coords = shipType.getShipCoordinates(coordinate, orientation);
            view.showPlacementPreview(coords, false, null);
        }
    }

    /**
     * Shows a placement preview for a ship at the specified coordinate.
     */
    public void previewPlacement(Coordinate coordinate) {
        Orientation orientation = view.getSelectedOrientation();
        ShipType shipType = view.getSelectedShipType();

        if (shipType == null) return;

        try {
            Ship ship = Ship.createShip(coordinate, orientation, shipType, fleetManager.getGrid());
            boolean valid = fleetManager.canPlaceShip(ship);
            view.showPlacementPreview(ship.getCoordinates(), valid, ship);
        } catch (IllegalArgumentException ex) {
            LinkedHashSet<Coordinate> coords = shipType.getShipCoordinates(coordinate, orientation);
            view.showPlacementPreview(coords, false, null);
        }
    }

    /**
     * Processe an incoming shot from the opponent.
     * Updates the local model, notifies the strategy, and refreshes the UI.
     */
    public boolean processIncomingShot(Coordinate coordinate) {
        boolean hit = fleetManager.handleIncomingShot(coordinate);

        gameModeStrategy.sendGridUpdate(
                fleetManager.getGrid(),
                fleetManager.getFleet(),
                hit
        );

        String gridSerialized = fleetManager.getSerializedGridState();
        view.updatePlayerGrid(gridSerialized, fleetManager.getFleet());
        return fleetManager.isGameOver();
    }

    /**
     * Transitions the state machine to the WaitingOpponentState.
     */
    public void transitionToWaitingOpponent() {
        transitionTo(new WaitingOpponentState());
    }

    /**
     * Transitions the state machine to the WaitingSetupState.
     */
    public void transitionToWaitingSetup() {
        transitionTo(new WaitingSetupState());
    }

    /**
     * Transitions the state machine to the GameOverState.
     */
    public void transitionToGameOver(boolean won, String message) {
        transitionTo(new GameOverState(won, message));
    }

    /**
     * Transitions the state machine to the ActiveTurnState.
     */
    public void transitionToActiveTurn() {
        transitionTo(new ActiveTurnState());
    }

    /**
     * Transitions to the given state.
     */
    void transitionTo(@NonNull TurnState newState) {
        Logger.log("TurnManager: " + currentState.getStateName() + " -> " + newState.getStateName());
        currentState.onExit(this);
        currentState = newState;
        currentState.onEnter(this);
    }

    // ===== Action handlers depend on the current state =====

    public void handlePlayerGridClick(Coordinate coordinate) {
        currentState.handlePlayerGridClick(this, coordinate);
    }

    public void handleOpponentGridClick(Coordinate coordinate) {
        currentState.handleOpponentGridClick(this, coordinate);
    }

    public void handlePlayerGridHover(Coordinate coordinate) {
        currentState.handlePlayerGridHover(this, coordinate);
    }

    public void handleOpponentGridHover(Coordinate coordinate) {
        currentState.handleOpponentGridHover(this, coordinate);
    }

    public void handleIncomingShot(Coordinate coordinate) {
        currentState.handleIncomingShot(this, coordinate);
    }

    public void handleOpponentGridUpdate(String grid, List<Ship> fleet) {
        opponentGrid.updateGridState(grid);

        currentState.handleOpponentGridUpdate(this, grid, fleet);
    }

    public void handleGameStatusReceived(GameState state) {
        currentState.handleGameStatusReceived(this, state);
    }

    public void handleGameOver(String message) {
        transitionTo(new GameOverState(true, message));
    }

    // ===== Query for the current state =====

    public boolean canShoot() {
        return currentState.canShoot();
    }

    public boolean canPlaceShip() {
        return currentState.canPlaceShip();
    }

    public String getCurrentStateName() {
        return currentState.getStateName();
    }

    // ==== Game interaction facade =====

    @Override
    public void requestShot(Coordinate coordinate) {
        handleOpponentGridClick(coordinate);
    }

    @Override
    public void requestShipPlacement(Coordinate coordinate) {
        handlePlayerGridClick(coordinate);
    }

    @Override
    public void requestPlacementPreview(Coordinate coordinate) {
        handlePlayerGridHover(coordinate);
    }

    @Override
    public void previewShot(Coordinate coordinate) {
        handleOpponentGridHover(coordinate);
    }

    // ===== Callbacks =====

    @FunctionalInterface
    public interface SetupCompleteCallback {
        void onSetupComplete();
    }
}
