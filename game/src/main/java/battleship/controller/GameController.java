package battleship.controller;

import battleship.handlers.AbstractPlayerCommunication;
import battleship.handlers.CommunicationEvents;
import battleship.model.FleetManager;
import battleship.model.Grid;
import battleship.view.GameView;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import lombok.Getter;
import lombok.NonNull;

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

    private final Grid grid;
    private final FleetManager fleetManager;
    @Getter
    private GameState gameState;
    private final GameView view;

    public GameController(@NonNull Grid grid,@NonNull FleetManager fleetManager,@NonNull GameView view) {
        this.grid = grid;
        this.fleetManager = fleetManager;
        this.view = view;
        this.gameState = GameState.SETUP;
    }
}
