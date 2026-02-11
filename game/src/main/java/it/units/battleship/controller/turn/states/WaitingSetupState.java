package it.units.battleship.controller.turn.states;

import it.units.battleship.controller.turn.TurnManager;
import it.units.battleship.GameState;

/**
 * State when the local player has completed setup but is waiting for the opponent to finish.
 * In this state, the player cannot interact with grids and must wait for a game start signal.
 */
public class WaitingSetupState extends BaseGameState {

    @Override
    public void onEnter(TurnManager manager) {
        super.onEnter(manager);
        manager.setPlayerTurn(false);
        manager.notifyUser("Waiting for opponent setup...");
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
    public void handleGameStatusReceived(TurnManager manager, GameState state) {
        manager.transitionToGamePhase();
        if (state == GameState.ACTIVE_TURN) {
            manager.transitionToActiveTurn();
        } else if (state == GameState.WAITING_FOR_OPPONENT) {
            manager.transitionToWaitingOpponent();
        }
    }
}
