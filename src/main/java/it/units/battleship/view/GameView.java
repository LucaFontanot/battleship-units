package it.units.battleship.view;

/**
 * Defines the contract for a view in the Battleship game, decoupling the from the game logic.
 *
 * This interface specifies the methods that any concrete view implementation (e.g., a Swing GUI)
 * must provide to interact with the GameController.
 * Its purpose is to present the current game state to the user and to solicit/receive user input.
 * Adhering to this interface ensures that the GameController remains independent of specific UI technologies.
 */

public interface GameView {
}
