package it.units.battleship.controller.turn.contracts;

/**
 * Responsibility: This manage how game state change from one to another.
 */
public interface StateTransitions {
    /** move the game to our active turn */
    void transitionToActiveTurn();
    /** wait for the opponent's move */
    void transitionToWaitingOpponent();
    /** waiting for everyone to place ships */
    void transitionToWaitingSetup();
    /** finish the match and show message to the user */
    void transitionToGameOver(boolean won, String message);
}
