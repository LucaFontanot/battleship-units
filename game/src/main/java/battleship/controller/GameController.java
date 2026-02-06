package battleship.controller;

import battleship.controller.actions.NetworkInputActions;
import battleship.controller.actions.NetworkOutputActions;
import battleship.controller.handlers.ui.OpponentGridHandler;
import battleship.controller.handlers.ui.PlayerGridHandler;
import battleship.controller.actions.GameInteractionFacade;
import battleship.controller.actions.GridInteractionObserver;
import battleship.model.*;
import battleship.view.GameView;
import it.units.battleship.*;
import battleship.model.Ship;
import it.units.battleship.data.socket.payloads.*;
import lombok.Getter;
import lombok.NonNull;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Orchestrates the game logic and coordinates communication between the game model,
 * the UI view, and the network communication layer.
 */
public class GameController implements NetworkInputActions, GameInteractionFacade {

    private final Grid grid;
    private final FleetManager fleetManager;

    @Getter
    private GameState gameState;
    private final GameView view;

    private final NetworkOutputActions networkOutput;
    /**
     * Handles player interaction events on the game grid during the game lifecycle.
     *
     * Responsibilities include:
     * - Responding to hover events on the player's grid during the setup phase.
     * - Handling click events to place ships during the setup phase.
     */
    GridInteractionObserver playerHandler;
    GridInteractionObserver opponentHandler;

    public GameController(@NonNull Grid grid,@NonNull FleetManager fleetManager, NetworkOutputActions networkOutput,@NonNull GameView view) {
        this.grid = grid;
        this.networkOutput = networkOutput;
        this.fleetManager = fleetManager;
        this.view = view;
        this.gameState = GameState.SETUP;

        this.opponentHandler = new OpponentGridHandler(this);
        this.playerHandler = new PlayerGridHandler(this);

        view.setOpponentGridListener(opponentHandler);
        view.setPlayerGridListener(playerHandler);
    }

    @Override
    public void requestShipPlacement(Coordinate coordinate) {
        if (gameState == GameState.SETUP){
            handleSetupClick(coordinate);
        }
    }

    private void handleSetupClick(Coordinate coordinate){
        Orientation selectedOrientation = view.getSelectedOrientation();
        ShipType selectedShipType = view.getSelectedShipType();

        if (selectedShipType == null) return;

        try {
            Ship ship = Ship.createShip(coordinate, selectedOrientation, selectedShipType, fleetManager.getGrid());
            boolean placed = fleetManager.addShip(ship);

            if (placed) {
                List<Ship> currentFleet = fleetManager.getFleet();

                view.updatePlayerGrid(grid.gridSerialization(), currentFleet);

                Map<ShipType, Integer> shipCounts = fleetManager.getPlacedCounts();
                Map<ShipType, Integer> fleetConfiguration = fleetManager.getRequiredFleetConfiguration();

                view.refreshFleetSelection(shipCounts, fleetConfiguration);
            } else {
                Toolkit.getDefaultToolkit().beep();
                view.showPlacementPreview(ship.getCoordinates(), false, ship);
            }
        } catch (IllegalArgumentException ex) {
            Toolkit.getDefaultToolkit().beep();

            LinkedHashSet<Coordinate> coords = selectedShipType.getShipCoordinates(coordinate,selectedOrientation);
            view.showPlacementPreview(coords, false, null);
        }
    }

    @Override
    public void requestPlacementPreview(Coordinate coordinate) {
        if (gameState == GameState.SETUP){
            handleSetupHover(coordinate);
        }
    }

    private void handleSetupHover(Coordinate coordinate){
        Orientation selectedOrientation = view.getSelectedOrientation();
        ShipType selectedShipType = view.getSelectedShipType();

        if (selectedShipType == null) return;
        try {
            Ship ship = Ship.createShip(coordinate, selectedOrientation, selectedShipType, fleetManager.getGrid());
            boolean valid = fleetManager.canPlaceShip(ship);

            view.showPlacementPreview(ship.getCoordinates(), valid, ship);

        } catch (IllegalArgumentException ex) {
            LinkedHashSet<Coordinate> coords = selectedShipType.getShipCoordinates(coordinate, selectedOrientation);
            view.showPlacementPreview(coords, false, null);
        }
    }

    @Override
    public void requestShot(Coordinate coordinate) {
        if(gameState == GameState.ACTIVE_TURN){
            handleOpponentGridClick(coordinate);
        }
    }

    private void handleOpponentGridClick(Coordinate coordinate){
        gameState = GameState.WAITING_FOR_OPPONENT;

        view.setPlayerTurn(false);
        networkOutput.sendShotRequest(coordinate);
    }

    @Override
    public void previewShot(Coordinate coordinate) {
        if (gameState == GameState.ACTIVE_TURN){
            view.showShotPreview(coordinate);
        }
    }

    public void startGame(){
        List<Ship> currentFleet = fleetManager.getFleet();
        Map<ShipType, Integer> shipCounts = currentFleet.stream()
                .collect(Collectors.groupingBy(
                        Ship::getShipType,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        Map<ShipType, Integer> fleetConfiguration = fleetManager.getRequiredFleetConfiguration(); // Saranno tutti 0
        view.refreshFleetSelection(shipCounts, fleetConfiguration);

    }

    /**
     * Updates the player's grid representation by refreshing the grid visualization and overlaying the fleet positions.
     *
     * @param gridSerialized A serialized string representation of the grid containing the current state
     *                       (e.g., hits, misses, empty cells).
     * @param shipFleet The list of ships composing the player's fleet, which will be rendered on the grid
     *                  along with their statuses (e.g., sunk, damaged).
     */
    public void updatePlayerGrid(String gridSerialized, List<Ship> shipFleet) {
        view.updatePlayerGrid(gridSerialized, shipFleet);
    }

    @Override
    public void processIncomingShot(Coordinate shotCoord) {
        boolean shotOutcome = fleetManager.handleIncomingShot(shotCoord);
        List<Ship> fleet = fleetManager.getFleet();

        networkOutput.sendGridUpdate(grid, fleet, shotOutcome);

        String gridSerialized = grid.gridSerialization();
        updatePlayerGrid(gridSerialized, fleetManager.getFleet());

        gameState = GameState.ACTIVE_TURN;
        view.setPlayerTurn(true);

        if (fleetManager.isGameOver()) {
            handleGameOver();
        }
    }

    private void handleGameOver() {
        gameState = GameState.GAME_OVER;
        String message = "You win!";
        networkOutput.sendGameStatus(gameState, message);
        view.showEndGamePhase("You lost! All your ships are sunk.");
        view.setPlayerTurn(false);
    }

    @Override
    public void processOpponentGridUpdate(String grid, List<Ship> revealedFleet) {
        view.updateOpponentGrid(grid, revealedFleet);
    }

    @Override
    public void processGameStatusUpdate(GameState newState, String message) {
        this.gameState = newState;
        switch (newState){
            case GAME_OVER -> {
                view.showEndGamePhase(message);
                view.setPlayerTurn(false);
            }
        }
    }
}