package battleship.controller;

import battleship.controller.actions.NetworkInputActions;
import battleship.controller.handlers.ui.OpponentGridHandler;
import battleship.controller.handlers.ui.PlayerGridHandler;
import battleship.controller.actions.GameInteractionFacade;
import battleship.controller.network.AbstractPlayerCommunication;
import battleship.controller.network.CommunicationEvents;
import battleship.controller.setup.SetupController;
import battleship.model.*;
import battleship.view.GameFrame;
import battleship.view.GameView;
import battleship.view.setup.SetupPanel;
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

    @Getter
    private final Grid grid;
    @Getter
    private final FleetManager fleetManager;

    @Getter
    private GameState gameState;
    private final GameView view = new GameFrame();
    private final AbstractPlayerCommunication communication;

    public GameController(@NonNull Grid grid, @NonNull FleetManager fleetManager, AbstractPlayerCommunication communication) {

        this.grid = grid;
        this.fleetManager = fleetManager;
        this.communication = communication;
        this.gameState = GameState.WAITING_FOR_SETUP;

        view.setOpponentGridListener(new OpponentGridHandler(this));
        view.setPlayerGridListener(new PlayerGridHandler(this));
    }

    @Override
    public void requestShot(Coordinate coordinate) {
        if (gameState == GameState.ACTIVE_TURN) {
            handleOpponentGridClick(coordinate);
        }
    }

    private void handleOpponentGridClick(Coordinate coordinate) {
        gameState = GameState.WAITING_FOR_OPPONENT;

        view.setPlayerTurn(false);
        communication.sendShotRequest(coordinate);
    }

    @Override
    public void previewShot(Coordinate coordinate) {
        if (gameState == GameState.ACTIVE_TURN) {
            view.showShotPreview(coordinate);
        }
    }

    public void setupGame(){
        SetupController setupController = new SetupController(this, fleetManager -> {
            checkOrWaitForOpponentSetup();
        });
        setupController.startSetup();
    }

    public void checkOrWaitForOpponentSetup() {
        //
    }

    public void startGame() {
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
     * @param shipFleet      The list of ships composing the player's fleet, which will be rendered on the grid
     *                       along with their statuses (e.g., sunk, damaged).
     */
    public void updatePlayerGrid(String gridSerialized, List<Ship> shipFleet) {
        view.updatePlayerGrid(gridSerialized, shipFleet);
    }

    @Override
    public void processIncomingShot(Coordinate shotCoord) {
        boolean shotOutcome = fleetManager.handleIncomingShot(shotCoord);
        List<Ship> fleet = fleetManager.getFleet();

        communication.sendGridUpdate(grid, fleet, shotOutcome);

        String gridSerialized = GridMapper.serialize(grid.getGrid());
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
        communication.sendGameStatus(gameState, message);
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
        switch (newState) {
            case GAME_OVER -> {
                view.showEndGamePhase(message);
                view.setPlayerTurn(false);
            }
        }
    }
}