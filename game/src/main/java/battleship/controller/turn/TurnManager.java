package battleship.controller.turn;

import battleship.controller.mode.GameModeStrategy;
import battleship.controller.turn.states.SetupState;
import battleship.model.FleetManager;
import battleship.model.Grid;
import battleship.view.GameView;
import it.units.battleship.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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

    @FunctionalInterface
    public interface SetupCompleteCallback{
        void onSetupComplete();
    }
}
