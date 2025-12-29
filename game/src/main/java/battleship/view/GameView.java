package battleship.view;

import battleship.model.Ship;

import java.util.List;

/**
 * Represents the view component of the Battleship game, responsible for displaying
 * the game's state and relaying information to the user. This interface defines methods
 * for updating the game interface with player and opponent grid states, messages, and
 * game-over notifications.
 *
 * Implementing classes should provide specific visual or textual representations
 * of the game state depending on the application's requirements (e.g., a graphical user
 * interface or a text-based command-line interface).
 */

public interface GameView {

    void show();

    void onGameStart();

    void updatePlayerGrid(String serializedGrid, List<Ship> fleet);

    void updateOpponentGrid(String serializedGrid);

    void updateMessage(String message);

    void showGameOver(String winner);

    void shipSunk(Ship ship);

}
