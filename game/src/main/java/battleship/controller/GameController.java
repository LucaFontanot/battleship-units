package battleship.controller;

import battleship.model.*;
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

    public void startGame(){
        view.showSetupPhase();
        view.updateSystemMessage("Place your ships on the grid.");
        view.updatePlayerGrid(grid.gridSerialization(), fleetManager.getFleet());
    }

    public void placeShip(@NonNull ShipType shipType,@NonNull Orientation orientation,@NonNull Coordinate coordinate){
        try {
            Ship ship = Ship.createShip(coordinate, orientation, shipType, grid);
            if (fleetManager.addShip(ship)){
                view.updateSystemMessage("Ship placed successfully.");
                view.updatePlayerGrid(grid.gridSerialization(), fleetManager.getFleet());
            }else {
                view.displayErrorAlert("Ship placement failed collision with other ships or goes out of the grid or is not part of the fleet configuration. Please try again.");
            }
        }catch (IllegalArgumentException e){
            view.displayErrorAlert("Invalid ship placement. Please try again.");
        }
    }

    public void removeShip(@NonNull Coordinate coordinate){
        if (fleetManager.removeShipByCoordinate(coordinate)){
            view.updateSystemMessage("Ship removed successfully.");
            view.updatePlayerGrid(grid.gridSerialization(), fleetManager.getFleet());
        }else {
            view.displayErrorAlert("Ship removal failed. Please try again.");
        }
    }

    public void confirmSetup(){
        if (fleetManager.isFleetComplete()){
            gameState = GameState.WAITING;
            view.showGamePhase();
        }else {
            view.displayErrorAlert("Please place all your ships before starting the game.");
        }
    }

    public void processShot(@NonNull Coordinate coordinate) {
        fleetManager.handleIncomingShot(coordinate);
        
        Ship ship = fleetManager.getShipByCoordinate(coordinate);
        if (ship != null && ship.isSunk()) {
            view.displayShipSunk(ship);
        }

        view.updatePlayerGrid(grid.gridSerialization(), fleetManager.getFleet());
    }
}