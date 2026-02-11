package it.units.battleship.controller.turn;

public interface StateTransitions {
    void transitionToActiveTurn();
    void transitionToWaitingOpponent();
    void transitionToWaitingSetup();
    void transitionToGameOver(boolean won, String message);
}
