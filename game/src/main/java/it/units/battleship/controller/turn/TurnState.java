package it.units.battleship.controller.turn;

import it.units.battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;

import java.util.List;

/**
 * Represents the current state of a turn in the game.
 * Each state handles only the actions that are relevant to its phase.
 */
public interface TurnState {
    /**
     * Called when the state is entered.
     */
    void onEnter();

    /**
     * Called when the state is exited.
     */
    void onExit();

    /**
     * Handle the click on the opponent's grid.
     */
    void handleOpponentGridClick(Coordinate coordinate);

    /**
     * Handle the click on the player's grid.
     */
    void handlePlayerGridClick(Coordinate coordinate);

    /**
     * Handle the hover on the opponent's grid.
     */
    void handleOpponentGridHover(Coordinate coordinate);

    /**
     * Handle the hover on the player's grid.
     */
    void handlePlayerGridHover(Coordinate coordinate);

    /**
     * Handle incoming shots.
     */
    void handleIncomingShot(Coordinate coordinate);

    /**
     * Handle update for the opponent's grid.
     */
    void handleOpponentGridUpdate(String grid, List<Ship> fleet);

    /**
     * Handle game status updates from server (e.g., game start signal).
     */
    void handleGameStatusReceived(GameState state);

    /**
     * Return the name of the state.
     */
    String getStateName();

    /**
     * Tells if the player can interact with the opponent's grid.
     */
    boolean canShoot();

    /**
     * Tells if the player can place ships
     */
    boolean canPlaceShip();
}