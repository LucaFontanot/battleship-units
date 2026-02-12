package it.units.battleship.controller;

import it.units.battleship.controller.game.actions.GameInteractionFacade;
import it.units.battleship.controller.game.ui.OpponentGridHandler;
import it.units.battleship.controller.game.ui.PlayerGridHandler;
import it.units.battleship.controller.mode.GameModeStrategy;
import it.units.battleship.controller.turn.TurnManager;
import it.units.battleship.controller.turn.adapters.GameViewMediator;
import it.units.battleship.controller.turn.adapters.NetworkAdapter;
import it.units.battleship.controller.turn.contracts.NetworkActions;
import it.units.battleship.controller.turn.contracts.ViewActions;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.view.core.BattleshipView;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Logger;
import lombok.NonNull;

import java.util.List;


/**
 * Main glue class that keeps the model, view and networking layer talking.
 */
public class GameController implements GameModeStrategy.GameModeCallback, GameInteractionFacade{

    private final TurnManager turnManager;
    private final GameModeStrategy gameMode;
    private final BattleshipView view;

    public GameController(@NonNull FleetManager fleetManager,
                          @NonNull GameModeStrategy gameMode,
                          @NonNull BattleshipView view) {
        this.gameMode = gameMode;
        this.view = view;

        Grid opponentGrid = new Grid(fleetManager.getGridRows(), fleetManager.getGridCols());

        ViewActions viewActions = new GameViewMediator(view, fleetManager, opponentGrid);
        NetworkActions networkActions = new NetworkAdapter(gameMode);

        this.turnManager = new TurnManager(viewActions, networkActions, fleetManager, opponentGrid);

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
        turnManager.handleGameStatusReceived(state, message);
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
}
