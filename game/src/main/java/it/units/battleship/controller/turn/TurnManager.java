package it.units.battleship.controller.turn;

import it.units.battleship.controller.turn.states.*;
import it.units.battleship.model.Ship;
import it.units.battleship.*;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * Manages the turn flow using the State pattern.
 * Context role in the pattern and coord the transactions between states.
 */
public class TurnManager {

    @Getter
    private TurnState currentState;
    @Getter
    private final GameActions actions;

    public TurnManager(@NonNull GameActions actions) {
        this.actions = actions;
        this.currentState = new SetupState();
    }

    /**
     * Starts the TurnManager, entering in the initial state.
     */
    public void start() {
        currentState.onEnter(actions);
    }

    /**
     * Transitions to the given state.
     */
    void transitionTo(@NonNull TurnState newState) {
        Logger.log("TurnManager: " + currentState.getStateName() + " -> " + newState.getStateName());
        currentState.onExit();
        currentState = newState;
        currentState.onEnter(actions);
    }

    /**
     * Transitions the state machine to the WaitingOpponentState.
     */
    public void transitionToWaitingOpponent() {
        transitionTo(new WaitingOpponentState());
    }

    /**
     * Transitions the state machine to the WaitingSetupState.
     */
    public void transitionToWaitingSetup() {
        transitionTo(new WaitingSetupState());
    }

    /**
     * Transitions the state machine to the GameOverState.
     */
    public void transitionToGameOver(boolean won, String message) {
        transitionTo(new GameOverState(won, message));
    }

    /**
     * Transitions the state machine to the ActiveTurnState.
     */
    public void transitionToActiveTurn() {
        transitionTo(new ActiveTurnState());
    }

    // ===== Action handlers depend on the current state =====

    public void handlePlayerGridClick(Coordinate coordinate) {
        currentState.handlePlayerGridClick(actions, coordinate);
    }

    public void handleOpponentGridClick(Coordinate coordinate) {
        currentState.handleOpponentGridClick(actions, coordinate);
    }

    public void handlePlayerGridHover(Coordinate coordinate) {
        currentState.handlePlayerGridHover(actions, coordinate);
    }

    public void handleOpponentGridHover(Coordinate coordinate) {
        currentState.handleOpponentGridHover(actions, coordinate);
    }

    public void handleIncomingShot(Coordinate coordinate) {
        currentState.handleIncomingShot(actions, coordinate);
    }

    public void handleOpponentGridUpdate(String grid, List<Ship> fleet) {
        currentState.handleOpponentGridUpdate(actions, grid, fleet);
    }

    public void handleGameStatusReceived(GameState state) {
        currentState.handleGameStatusReceived(actions, state);
    }

    public void handleGameOver(String message) {
        transitionTo(new GameOverState(true, message));
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
