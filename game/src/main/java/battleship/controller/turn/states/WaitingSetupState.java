package battleship.controller.turn.states;

import battleship.controller.turn.TurnManager;
import battleship.controller.turn.TurnState;
import it.units.battleship.GameState;

/**
 * State when the local player has completed setup but is waiting for the opponent to finish.
 * In this state, the player cannot interact with grids and must wait for a game start signal.
 */
public class WaitingSetupState implements TurnState {

    @Override
    public void onEnter(TurnManager manager) {
        TurnState.super.onEnter(manager);
        manager.getView().setPlayerTurn(false);
        manager.getView().showSystemMessage("Waiting for opponent setup...");
    }

    @Override
    public String getStateName() {
        return GameState.WAITING_SETUP.name();
    }

    /**
     * Handles game status updates from the server.
     * When both players are ready, the server sends the actual starting state.
     */
    public void handleGameStatusReceived(TurnManager manager, GameState state) {
        if (state == GameState.ACTIVE_TURN) {
            manager.transitionTo(new ActiveTurnState());
        } else if (state == GameState.WAITING_FOR_OPPONENT) {
            manager.transitionTo(new WaitingOpponentState());
        }
    }
}
