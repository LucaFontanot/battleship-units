package battleship.controller.turn;

import battleship.controller.mode.GameModeStrategy;
import battleship.controller.turn.states.SetupState;
import battleship.model.FleetManager;
import battleship.model.Grid;
import battleship.model.Ship;
import battleship.view.GameView;
import it.units.battleship.Coordinate;
import it.units.battleship.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

/**
 * Manages the turn flow using the State pattern.
 * Context role in the pattern and coord the transactions between states.
 */
public class TurnManager {
    @Getter
    private TurnState currentState;
    @Getter
    private final Grid grid;
    @Getter
    private final FleetManager  fleetManager;
    @Getter
    private final GameView view;
    @Getter
    private final GameModeStrategy gameModeStrategy;
    @Setter
    private SetupCompleteCallback setupCompleteCallback;

    public TurnManager(@NonNull Grid grid,
                       @NonNull FleetManager fleetManager,
                       @NonNull GameView view,
                       @NonNull GameModeStrategy gameModeStrategy) {
        this.grid = grid;
        this.fleetManager = fleetManager;
        this.gameModeStrategy = gameModeStrategy;
        this.view = view;

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
        currentState.handleOpponentGridUpdate(this, grid, fleet);
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

    @FunctionalInterface
    public interface SetupCompleteCallback{
        void onSetupComplete();
    }
}
