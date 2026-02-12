package it.units.battleship.controller.turn;

import it.units.battleship.controller.turn.contracts.NetworkActions;
import it.units.battleship.controller.turn.contracts.SetupInputProvider;
import it.units.battleship.controller.turn.contracts.StateTransitions;
import it.units.battleship.controller.turn.contracts.ViewActions;
import it.units.battleship.controller.turn.states.*;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.*;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * Manages the turn flow using the State pattern.
 * Context role in the pattern and coord the transactions between states.
 */
public class TurnManager implements StateTransitions {

    @Getter
    private TurnState currentState;
    private final GameContext context;

    public TurnManager(@NonNull ViewActions view,
                       @NonNull SetupInputProvider setupInput,
                       @NonNull NetworkActions network,
                       @NonNull FleetManager fleetManager,
                       @NonNull Grid opponentGrid) {
        this.context = new GameContext(view, this, network, fleetManager, opponentGrid);
        this.currentState = new SetupState(context, setupInput);
    }

    /**
     * Starts the TurnManager, entering in the initial state.
     */
    public void start() {
        currentState.onEnter();
    }

    /**
     * Transitions to the given state.
     */
    public void transitionTo(@NonNull TurnState newState) {
        Logger.log("TurnManager: " + currentState.getStateName() + " -> " + newState.getStateName());
        currentState.onExit();
        currentState = newState;
        currentState.onEnter();
    }

    /**
     * Transitions the state machine to the WaitingOpponentState.
     */
    @Override
    public void transitionToWaitingOpponent() {
        transitionTo(new WaitingOpponentState(context));
    }

    /**
     * Transitions the state machine to the WaitingSetupState.
     */
    @Override
    public void transitionToWaitingSetup() {
        transitionTo(new WaitingSetupState(context));
    }

    /**
     * Transitions the state machine to the GameOverState.
     */
    @Override
    public void transitionToGameOver(boolean won, String message) {
        transitionTo(new GameOverState(context, won, message));
    }

    /**
     * Transitions the state machine to the ActiveTurnState.
     */
    @Override
    public void transitionToActiveTurn() {
        transitionTo(new ActiveTurnState(context));
    }

    // ===== Action handlers depend on the current state =====

    public void handlePlayerGridClick(Coordinate coordinate) {
        currentState.handlePlayerGridClick(coordinate);
    }

    public void handleOpponentGridClick(Coordinate coordinate) {
        currentState.handleOpponentGridClick(coordinate);
    }

    public void handlePlayerGridHover(Coordinate coordinate) {
        currentState.handlePlayerGridHover(coordinate);
    }

    public void handleOpponentGridHover(Coordinate coordinate) {
        currentState.handleOpponentGridHover(coordinate);
    }

    public void handleIncomingShot(Coordinate coordinate) {
        currentState.handleIncomingShot(coordinate);
    }

    public void handleOpponentGridUpdate(String grid, List<Ship> fleet) {
        currentState.handleOpponentGridUpdate(grid, fleet);
    }

    public void handleGameStatusReceived(GameState state, String message) {
        currentState.handleGameStatusReceived(state, message);
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
}
