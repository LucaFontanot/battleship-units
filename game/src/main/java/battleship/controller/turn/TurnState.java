package battleship.controller.turn;

import battleship.model.Ship;
import it.units.battleship.Coordinate;

import java.util.List;

/**
 * Represents the current state of a turn in the game.
 * Each state handles only the actions that are relevant to its phase.
 */
public interface TurnState {
    /**Called when the state is entered.*/
    void onEnter(TurnManager turnManager);

    /**Called when the state is exited.*/
    default void onExit(){}

    /**Handle the click on the opponent's grid.*/
    default void handleOpponentGridClick(TurnManager turnManager, Coordinate coordinate){}

    /**Handle the click on the player's grid.*/
    default void handlePlayerGridClick(TurnManager turnManager, Coordinate coordinate){}

    /**Handle the hover on the opponent's grid.*/
    default void handleOpponentGridHover(TurnManager turnManager, Coordinate coordinate){}

    /**Handle the hover on the player's grid.*/
    default void handlePlayerGridHover(TurnManager turnManager, Coordinate coordinate){}

    /**Handle incoming shots.*/
    default void handleIncomingShot(TurnManager turnManager, Coordinate coordinate){}

    /**Handle update for the opponent's grid.*/
    default void handleOpponentGridUpdate(TurnManager turnManager, String grid, List<Ship> fleet){}

    /**Return the name of the state.*/
    String getStateName();

    /**Tells if the player can interact with the opponent's grid.*/
    default boolean canShoot(){return false;}

    /**Tells if the player can place ships*/
    default boolean canPlaceShip(){return false;}
}
