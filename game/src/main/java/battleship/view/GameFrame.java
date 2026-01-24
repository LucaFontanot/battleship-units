package battleship.view;

import battleship.model.Ship;

import java.util.List;

/**
 * Represents the main window (frame) of the Battleship game's graphical user interface (GUI).
 *
 * components that make up the game's visual presentation. It implements GameView
 * interface, providing a concrete Swing-based implementation for renderin the game state
 * and interacting with the user.
 *
 * Responsibilities include:
 * - Setting up the main window properties (title, size, close operation).
 * - Containing and managing other GUI components, such as panels for game grids, status messages,
 *   and control buttons.
 * - Translating user interactions (e.g., mouse clicks on grid cells) into events that can be
 *   processed by the GameController.
 * - Visually rendering the game grids and other dynamic elements based on updates from the model.
 *
 * The actual rendering logic for the grids and handling of specific
 component events will
 * reside within this class or dedicated sub-panels that it manages.
 */


public class GameFrame implements GameView{
    @Override
    public void show() {

    }

    @Override
    public void showSetupPhase() {

    }

    @Override
    public void showGamePhase() {

    }

    @Override
    public void updatePlayerGrid(String serializedGrid, List<Ship> fleet) {

    }

    @Override
    public void updateOpponentGrid(String serializedGrid) {

    }

    @Override
    public void updateSystemMessage(String message) {

    }

    @Override
    public void displayErrorAlert(String message) {

    }

    @Override
    public void showEndGamePhase(String winner) {

    }

    @Override
    public void displayShipSunk(Ship ship) {

    }

    @Override
    public void setPlayerTurn(boolean isPlayerTurn) {

    }
}
