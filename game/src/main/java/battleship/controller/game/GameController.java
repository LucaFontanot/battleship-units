package battleship.controller.game;

import battleship.controller.game.actions.NetworkInputActions;
import battleship.controller.game.events.CommunicationEvents;
import battleship.controller.game.network.AbstractPlayerCommunication;
import battleship.controller.game.ui.OpponentGridHandler;
import battleship.controller.game.ui.PlayerGridHandler;
import battleship.controller.game.actions.GameInteractionFacade;
import battleship.controller.setup.SetupController;
import battleship.model.game.FleetManager;
import battleship.model.game.Grid;
import battleship.serializer.GameDataMapper;
import battleship.view.game.GameFrame;
import battleship.view.game.GameView;
import battleship.view.welcome.WelcomeUi;
import it.units.battleship.*;
import battleship.model.game.Ship;
import it.units.battleship.data.socket.GameMessageType;
import it.units.battleship.data.socket.payloads.*;
import lombok.Getter;
import lombok.NonNull;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Orchestrates the game logic and coordinates communication between the game model,
 * the UI view, and the network communication layer.
 */
public class GameController implements NetworkInputActions, GameInteractionFacade, CommunicationEvents {

    @Getter
    private final Grid grid;
    @Getter
    private final FleetManager fleetManager;

    @Getter
    private GameState gameState;
    private final GameView view = new GameFrame();
    private final AbstractPlayerCommunication communication;
    private Runnable closeSetupUi = null;

    private boolean localReady = false;
    private boolean remoteReady = false;
    private boolean gameStarted = false;

    public GameController(@NonNull Grid grid, @NonNull FleetManager fleetManager, AbstractPlayerCommunication communication) {
        this.grid = grid;
        this.fleetManager = fleetManager;
        this.communication = communication;
        this.gameState = GameState.WAITING_FOR_SETUP;

        view.setOpponentGridListener(new OpponentGridHandler(this));
        view.setPlayerGridListener(new PlayerGridHandler(this));
        communication.addCommunicationEventsListener(this);

        view.setReturnToMenuAction(() -> {
            ((JFrame) view).dispose();
            new WelcomeUi().show();
        });
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
        this.closeSetupUi = setupController::close;
        setupController.startSetup();
    }

    public void checkOrWaitForOpponentSetup() {
        localReady = true;

        boolean iAmSecondReady = remoteReady;

        gameState = GameState.WAITING_FOR_OPPONENT;

        GameConfigDTO config = GameDataMapper.toGameConfigDTO(
                grid.getRow(),
                grid.getCol(),
                fleetManager.getRequiredFleetConfiguration()
        );

        communication.sendMessage(GameMessageType.GAME_SETUP, config);
        Logger.log("SENT GAME_SETUP (ready) rows=" + config.rows() + " cols=" + config.cols());

        if (iAmSecondReady) {
            ensureGameStarted();
            decideAndBroadcastFirstTurn();
        } else {
            view.showSystemMessage("Waiting for opponent...");
            view.setPlayerTurn(false);
        }
    }

    private void decideAndBroadcastFirstTurn() {
        boolean iStart = new java.util.Random().nextBoolean();

        if (iStart) {
            processGameStatusUpdate(GameState.ACTIVE_TURN, "Your turn");
            communication.sendGameStatus(GameState.WAITING_FOR_OPPONENT, "Opponent's turn");
        } else {
            processGameStatusUpdate(GameState.WAITING_FOR_OPPONENT, "Opponent's turn");
            communication.sendGameStatus(GameState.ACTIVE_TURN, "Your turn");
        }
    }


    public void startGame() {
        gameState = GameState.WAITING_FOR_OPPONENT;
        view.open();
        view.showGamePhase();
        view.setPlayerTurn(false);

        // render player grid immediately
        String myGridSerialized = GridMapper.serialize(grid.getGrid());
        updatePlayerGrid(myGridSerialized, fleetManager.getFleet());

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

        if (fleetManager.isGameOver()) {
            handleGameOver();
            return;
        }

        gameState = GameState.ACTIVE_TURN;
        view.setPlayerTurn(true);

        communication.sendGameStatus(GameState.WAITING_FOR_OPPONENT, "Opponent's turn");
    }

    private void handleGameOver() {
        gameState = GameState.GAME_OVER;
        view.setPlayerTurn(false);

        // I have lost
        view.showSystemMessage("You lost!");
        view.showEndGamePhase("You lost!");

        // Opponent has won
        communication.sendGameStatus(GameState.GAME_OVER, "You win!");
    }

    @Override
    public void processOpponentGridUpdate(String grid, List<Ship> revealedFleet) {
        view.updateOpponentGrid(grid, revealedFleet);
    }

    @Override
    public void processGameStatusUpdate(GameState newState, String message) {
        this.gameState = newState;
        switch (newState) {
            case ACTIVE_TURN -> {
                view.setPlayerTurn(true);
                view.showSystemMessage(message != null ? message : "Your turn");
            }
            case WAITING_FOR_OPPONENT -> {
                view.setPlayerTurn(false);
                view.showSystemMessage(message != null ? message : "Opponent's turn");
            }
            case GAME_OVER -> {
                view.setPlayerTurn(false);
                view.showEndGamePhase(message != null ? message : "Game Over");
            }
        }
    }

    @Override
    public void onPlayerMessage(String playerName, String message) {
        // optional: view.showSystemMessage(playerName + ": " + message);
    }

    @Override
    public void onOpponentGridUpdate(GridUpdateDTO dto) {
        SwingUtilities.invokeLater(() -> {
            processOpponentGridUpdate(
                    GameDataMapper.toGridSerialized(dto),
                    GameDataMapper.toShipList(dto)
            );

            boolean hit = GameDataMapper.toShotOutcome(dto);
            view.showSystemMessage(hit ? "Hit!" : "Miss!");
        });
    }

    @Override
    public void onShotReceived(ShotRequestDTO dto) {
        SwingUtilities.invokeLater(() ->
                processIncomingShot(GameDataMapper.toCoordinate(dto))
        );
    }

    @Override
    public void onGameSetupReceived(GameConfigDTO dto) {
        SwingUtilities.invokeLater(() -> {
            remoteReady = true;
            Logger.log("RECEIVED GAME_SETUP (opponent ready)");

            if (localReady) {
                ensureGameStarted();
                view.showSystemMessage("Waiting for turn decision...");
                view.setPlayerTurn(false);
            }
        });
    }

    @Override
    public void onGameStatusReceived(GameStatusDTO dto) {
        SwingUtilities.invokeLater(() -> {
            ensureGameStarted();

            GameState state = GameDataMapper.toGameState(dto);
            String message = GameDataMapper.toMessage(dto);
            processGameStatusUpdate(state, message);
        });
    }

    private void ensureGameStarted() {
        if (gameStarted) return;
        gameStarted = true;

        if (closeSetupUi != null) closeSetupUi.run();

        view.open();
        view.showGamePhase();

        updatePlayerGrid(
                GridMapper.serialize(grid.getGrid()),
                fleetManager.getFleet()
        );

        view.setPlayerTurn(false);
        view.showSystemMessage("Game started. Waiting...");
        gameState = GameState.WAITING_FOR_OPPONENT;
    }
}