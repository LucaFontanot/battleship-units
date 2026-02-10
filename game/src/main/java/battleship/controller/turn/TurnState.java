package battleship.controller.turn;

import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;

import java.util.List;

/**
 * Represents the current state of a turn in the game.
 * Each state handles only the actions that are relevant to its phase.
 */
public interface TurnState {
    /**Called when the state is entered.*/
    void onEnter(TurnManager manager);

    /**Called when the state is exited.*/
    void onExit(TurnManager manager);

    /**Handle the click on the opponent's grid.*/
    void handleOpponentGridClick(TurnManager manager, Coordinate coordinate);

    /**Handle the click on the player's grid.*/
    void handlePlayerGridClick(TurnManager manager, Coordinate coordinate);

    /**Handle the hover on the opponent's grid.*/
    void handleOpponentGridHover(TurnManager manager, Coordinate coordinate);

    /**Handle the hover on the player's grid.*/
    void handlePlayerGridHover(TurnManager manager, Coordinate coordinate);

    /**Handle incoming shots.*/
    void handleIncomingShot(TurnManager manager, Coordinate coordinate);

    /**Handle update for the opponent's grid.*/
    void handleOpponentGridUpdate(TurnManager manager, String grid, List<Ship> fleet);

    /**Handle game status updates from server (e.g., game start signal).*/
    void handleGameStatusReceived(TurnManager manager, GameState state);

    /**Return the name of the state.*/
    String getStateName();

    /**Tells if the player can interact with the opponent's grid.*/
    boolean canShoot();

    /**Tells if the player can place ships*/
    boolean canPlaceShip();
}