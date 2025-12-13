package battleship.controller;

/**
 * Manages the core game flow and logic of the Battleship game.
 *
 * This class acts as the central controller in the architecture, orchestrating the interaction
 * between the game model (Grid, Ships) and the view (User Interface). Its primary responsibilities include:
 *
 * - Game Loop: Executing the main cycle of the game until a termination condition is met.
 * - Turn Management: Handling the sequence of player turns.
 * - Input Handling: Requesting and processing player actions (e.g., coordinates for a strike) via the View.
 * - Win Condition Verification: Checking the state of the game after each move to determine if a player has won
 *     (e.g., by calling isSunk() on all ships in the navy).
 */

public class GameController {
}
