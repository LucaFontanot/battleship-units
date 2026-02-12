package it.units.battleship.controller.turn.states;

import it.units.battleship.GameState;
import it.units.battleship.controller.turn.GameContext;

/**
 * State when the local player has completed setup but is waiting for the opponent to finish.
 * In this state, the player cannot interact with grids and must wait for a game start signal.
 */
public class WaitingSetupState extends BaseGameState {

    public WaitingSetupState(GameContext ctx) {
        super(ctx);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        view.setPlayerTurn(false);
        view.notifyUser("Waiting for opponent setup...");
    }

    @Override
    public String getStateName() {
        return GameState.WAITING_SETUP.name();
    }

    /**
     * Handles game status updates from the server.
     * When both players are ready, the server sends the actual starting state.
     */
    @Override
    public void handleGameStatusReceived(GameState state) {
        view.transitionToGamePhase();
        if (state == GameState.ACTIVE_TURN) {
            stateTransitions.transitionToActiveTurn();
        } else if (state == GameState.WAITING_FOR_OPPONENT) {
            stateTransitions.transitionToWaitingOpponent();
        }
    }
}
