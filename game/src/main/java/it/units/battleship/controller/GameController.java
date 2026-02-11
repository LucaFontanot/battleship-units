package it.units.battleship.controller;

import it.units.battleship.CellState;
import it.units.battleship.controller.game.actions.GameInteractionFacade;
import it.units.battleship.controller.game.ui.OpponentGridHandler;
import it.units.battleship.controller.game.ui.PlayerGridHandler;
import it.units.battleship.controller.mode.GameModeStrategy;
import it.units.battleship.controller.turn.GameActions;
import it.units.battleship.controller.turn.TurnManager;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.view.core.BattleshipView;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Logger;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import lombok.NonNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;


/**
 * Main glue class that keeps the model, view and networking layer talking.
 * Implements {@link GameActions} so that turn states can call back into the controller
 * for model updates, view refreshes, network communication and state transitions.
 */
public class GameController implements GameModeStrategy.GameModeCallback, GameInteractionFacade, GameActions {

    private final TurnManager turnManager;
    private final GameModeStrategy gameMode;
    private final BattleshipView view;
    private final FleetManager fleetManager;
    private final Grid opponentGrid;

    public GameController(@NonNull FleetManager fleetManager,
                          @NonNull GameModeStrategy gameMode,
                          @NonNull BattleshipView view) {
        this.gameMode = gameMode;
        this.view = view;
        this.fleetManager = fleetManager;

        this.opponentGrid = new Grid(fleetManager.getGridRows(), fleetManager.getGridCols());

        this.turnManager = new TurnManager(this);

        view.setOpponentGridListener(new OpponentGridHandler(this));
        view.setPlayerGridListener(new PlayerGridHandler(this));

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

    // ===== GameInteractionFacade implementation (View -> Controller) =====

    @Override
    public void requestShot(Coordinate coordinate) {
        turnManager.handleOpponentGridClick(coordinate);
    }

    @Override
    public void previewShot(Coordinate coordinate) {
        turnManager.handleOpponentGridHover(coordinate);
    }

    @Override
    public void requestShipPlacement(Coordinate coordinate) {
        turnManager.handlePlayerGridClick(coordinate);
    }

    @Override
    public void requestPlacementPreview(Coordinate coordinate) {
        turnManager.handlePlayerGridHover(coordinate);
    }

    // ===== GameActions: model query =====

    @Override
    public CellState getOpponentCellState(Coordinate coordinate) {
        return opponentGrid.getState(coordinate);
    }

    // ===== GameActions: game actions =====

    @Override
    public void placeShip(Coordinate coordinate) {
        Orientation orientation = view.getSelectedOrientation();
        ShipType shipType = view.getSelectedShipType();

        if (shipType == null) return;

        try {
            Ship ship = Ship.createShip(coordinate, orientation, shipType, fleetManager.getGrid());
            boolean placed = fleetManager.addShip(ship);

            if (placed) {
                refreshPlayerGrid();
                refreshFleetUI();

                if (fleetManager.isFleetComplete()) {
                    gameMode.notifySetupComplete();
                    transitionToWaitingSetup();
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

    @Override
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

    @Override
    public void fireShot(Coordinate coordinate) {
        gameMode.sendShot(coordinate);
    }

    @Override
    public boolean processIncomingShot(Coordinate coordinate) {
        boolean hit = fleetManager.handleIncomingShot(coordinate);

        gameMode.sendGridUpdate(
                fleetManager.getGrid(),
                fleetManager.getFleet(),
                hit
        );

        String gridSerialized = fleetManager.getSerializedGridState();
        view.updatePlayerGrid(gridSerialized, fleetManager.getFleet());
        return fleetManager.isGameOver();
    }

    // ===== GameActions: view =====

    @Override
    public void setPlayerTurn(boolean isPlayerTurn) {
        view.setPlayerTurn(isPlayerTurn);
    }

    @Override
    public void notifyUser(String message) {
        view.showSystemMessage(message);
    }

    @Override
    public void refreshPlayerGrid() {
        String gridSerialized = fleetManager.getSerializedGridState();
        view.updatePlayerGrid(gridSerialized, fleetManager.getFleet());
    }

    @Override
    public void refreshFleetUI() {
        Map<ShipType, Integer> placedCounts = fleetManager.getPlacedCounts();
        Map<ShipType, Integer> requiredCounts = fleetManager.getRequiredFleetConfiguration();
        view.refreshFleetSelection(placedCounts, requiredCounts);
    }

    @Override
    public void showShotPreview(Coordinate coordinate) {
        view.showShotPreview(coordinate);
    }

    @Override
    public void showEndGame(String message) {
        view.showEndGamePhase(message);
    }

    @Override
    public void transitionToGamePhase() {
        view.transitionToGamePhase();
    }

    @Override
    public void updateOpponentGrid(String grid, List<Ship> fleet) {
        opponentGrid.updateGridState(grid);
        view.updateOpponentGrid(grid, fleet);
    }

    @Override
    public void playerErrorSound() {
        view.playerErrorSound();
    }

    @Override
    public void showPlacementPreview(LinkedHashSet<Coordinate> coordinates, boolean valid, Ship ship) {
        view.showPlacementPreview(coordinates, valid, ship);
    }

    // ===== GameActions: network =====

    @Override
    public void sendGameOver(String message) {
        gameMode.sendGameOver(message);
    }

    // ===== GameActions: state transitions =====

    @Override
    public void transitionToActiveTurn() {
        turnManager.transitionToActiveTurn();
    }

    @Override
    public void transitionToWaitingOpponent() {
        turnManager.transitionToWaitingOpponent();
    }

    @Override
    public void transitionToWaitingSetup() {
        turnManager.transitionToWaitingSetup();
    }

    @Override
    public void transitionToGameOver(boolean won, String message) {
        turnManager.transitionToGameOver(won, message);
    }
}
