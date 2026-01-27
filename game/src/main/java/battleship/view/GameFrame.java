package battleship.view;

import battleship.model.Ship;
import battleship.view.grid.GridUI;
import lombok.Getter;

import java.util.List;

/**
 * The main Swing application window (JFrame) for the Battleship game.
 * Implements the {@link GameView} interface to bridge the gap between the game logic (Controller)
 * and the user interface.
 *
 * Responsibilities:
 *  - Acts as the root container for all visual components (Player Grid, Opponent Grid).
 *  - Delegates specific rendering tasks to specialized components.
 *  - Manages the high-level layout of the application.
 */
public class GameFrame implements GameView{
    @Getter
    private final GridUI playerGridUI;
    @Getter
    private final GridUI opponentGridUI;

    public GameFrame(GridUI playerGridUI, GridUI opponentGridUI) {
        this.playerGridUI = playerGridUI;
        this.opponentGridUI = opponentGridUI;
    }

    @Override
    public void updatePlayerGrid(String gridSerialized, List<Ship> fleetToRender) {
        this.playerGridUI.displayData(gridSerialized, fleetToRender);
    }

    @Override
    public void updateOpponentGrid(String gridSerialized, List<Ship> fleetToRender) {
        this.opponentGridUI.displayData(gridSerialized, fleetToRender);
    }
}