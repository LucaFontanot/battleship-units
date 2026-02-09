package battleship.controller.turn;

import battleship.controller.game.actions.GameInteractionFacade;
import battleship.controller.mode.GameModeStrategy;
import battleship.controller.turn.states.SetupState;
import battleship.model.*;
import battleship.view.core.BattleshipView;
import it.units.battleship.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

/**
 * Manages the turn flow using the State pattern.
 * Context role in the pattern and coord the transactions between states.
 */
public class TurnManager implements GameInteractionFacade {
    @Getter
    private TurnState currentState;
    @Getter
    private final Grid grid;
    @Getter@Setter
    private Grid opponentGrid;
    @Getter
    private final FleetManager  fleetManager;
    @Getter
    private final BattleshipView view;
    @Getter
    private final GameModeStrategy gameModeStrategy;
    @Setter
    private SetupCompleteCallback setupCompleteCallback;
    @Setter
    private PhaseTransitionCallback phaseTransitionCallback;

    public TurnManager(@NonNull Grid grid,
                       @NonNull FleetManager fleetManager,
                       @NonNull BattleshipView view,
                       @NonNull GameModeStrategy gameModeStrategy) {
        this.grid = grid;
        this.fleetManager = fleetManager;
        this.gameModeStrategy = gameModeStrategy;
        this.view = view;
        //using grid.getRow and grid.getCol indeed using the default values,
        //oppoennt grid will scale automatically if diff grid size are implemented
        this.opponentGrid = new Grid(grid.getRow(), grid.getCol());

        this.currentState = new SetupState();
    }

    /**
     * Starts the TurnManager, entering in the initial state.
     */
    public void start(){
        currentState.onEnter(this);
    }

    /**
     * Called when the game setup is complete.
     */
    public void onSetupComplete(){
        if(setupCompleteCallback != null){
            setupCompleteCallback.onSetupComplete();
        }
    }

    /**
     * Called when transitioning from setup to game phase
     */
    public void onPhaseTransition(GamePhase newPhase){
        if (phaseTransitionCallback != null) {
            phaseTransitionCallback.onPhaseTransition(newPhase);
        }
    }

    /**
     * Transitions to the given state.
     */
    public void transitionTo(@NonNull TurnState newState){
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
        CellState[][] newOpponentGrid = GridMapper.deserialize(grid, opponentGrid.getCol(), opponentGrid.getRow());
        setOpponentGrid(new Grid(newOpponentGrid));

        currentState.handleOpponentGridUpdate(this, grid, fleet);
    }

    public void handleGameStatusReceived(GameState state) {
        currentState.handleGameStatusReceived(this, state);
    }

    public void handleGameOver(String message) {
        transitionTo(new battleship.controller.turn.states.GameOverState(true, message));
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
    public interface PhaseTransitionCallback{
        void onPhaseTransition(GamePhase phase);
    }

    @FunctionalInterface
    public interface SetupCompleteCallback{
        void onSetupComplete();
    }

    public enum GamePhase{
        SETUP, WAITING_SETUP, ACTIVE_TURN, GAME_OVER
    }
}
