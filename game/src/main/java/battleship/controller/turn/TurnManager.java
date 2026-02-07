package battleship.controller.turn;

import battleship.controller.turn.states.SetupState;
import battleship.model.FleetManager;
import battleship.model.Grid;
import battleship.view.GameView;
import lombok.Getter;
import lombok.NonNull;

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
    private SetupCompleteCallback setupCompleteCallback;

    public TurnManager(@NonNull Grid grid,
                       @NonNull FleetManager fleetManager,
                       @NonNull GameView view) {
        this.grid = grid;
        this.fleetManager = fleetManager;
        this.view = view;

        this.currentState = new SetupState();
    }

    public void onSetupComplete(){
        if(setupCompleteCallback != null){
            setupCompleteCallback.onSetupComplete();
        }
    }

    @FunctionalInterface
    public interface SetupCompleteCallback{
        void onSetupComplete();
    }
}
