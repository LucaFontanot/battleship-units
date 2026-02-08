package battleship.controller.turn;

import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Logger;

import java.util.List;

/**
 * Represents the current state of a turn in the game.
 * Each state handles only the actions that are relevant to its phase.
 */
public interface TurnState {
    /**Called when the state is entered.*/
    default void onEnter(TurnManager manager){
        Logger.debug("Entering state: " + getStateName());
    }

    /**Called when the state is exited.*/
    default void onExit(TurnManager manager){
        Logger.debug("Exiting state: " + getStateName());
    }

    /**Handle the click on the opponent's grid.*/
    default void handleOpponentGridClick(TurnManager manager, Coordinate coordinate){}

    /**Handle the click on the player's grid.*/
    default void handlePlayerGridClick(TurnManager manager, Coordinate coordinate){}

    /**Handle the hover on the opponent's grid.*/
    default void handleOpponentGridHover(TurnManager manager, Coordinate coordinate){}

    /**Handle the hover on the player's grid.*/
    default void handlePlayerGridHover(TurnManager manager, Coordinate coordinate){}

    /**Handle incoming shots.*/
    default void handleIncomingShot(TurnManager manager, Coordinate coordinate){}

    /**Handle update for the opponent's grid.*/
    default void handleOpponentGridUpdate(TurnManager manager, String grid, List<Ship> fleet){
        manager.getView().updateOpponentGrid(grid, fleet);
    }

    /**Handle game status updates from server (e.g., game start signal).*/
    default void handleGameStatusReceived(TurnManager manager, GameState state){}

    /**Return the name of the state.*/
    String getStateName();

    /**Tells if the player can interact with the opponent's grid.*/
    default boolean canShoot(){return false;}

    /**Tells if the player can place ships*/
    default boolean canPlaceShip(){return false;}
}
