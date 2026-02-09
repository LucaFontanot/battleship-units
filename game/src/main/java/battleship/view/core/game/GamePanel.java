package battleship.view.core.game;

import battleship.controller.game.actions.GridInteractionObserver;
import battleship.model.Ship;
import battleship.view.grid.GridUI;
import it.units.battleship.Coordinate;
import lombok.Getter;

import javax.swing.*;

import java.awt.*;
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
public class GamePanel extends JPanel implements GameView{
    @Getter
    private final GridUI playerGridUI;
    @Getter
    private final GridUI opponentGridUI;

    private final JPanel statusPanel;
    private final JLabel turnIndicator;
    private final JLabel systemMessage = new JLabel(" ");

    public GamePanel(int rows, int cols) {
        setLayout(new BorderLayout(10, 10));

        // Status bar at top
        statusPanel = new JPanel(new BorderLayout());
        systemMessage.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        systemMessage.setHorizontalAlignment(SwingConstants.CENTER);

        turnIndicator = new JLabel("Your turn");
        turnIndicator.setHorizontalAlignment(SwingConstants.CENTER);
        turnIndicator.setFont(new Font("Arial", Font.BOLD, 16));

        statusPanel.add(turnIndicator, BorderLayout.NORTH);
        statusPanel.add(systemMessage, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.NORTH);

        // Grids panel
        JPanel gridsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        gridsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Player grid (left)
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.add(new JLabel("Your Fleet", SwingConstants.CENTER), BorderLayout.NORTH);
        playerGridUI = new GridUI(rows, cols);
        playerPanel.add(playerGridUI, BorderLayout.CENTER);

        // Opponent grid (right)
        JPanel opponentPanel = new JPanel(new BorderLayout());
        opponentPanel.add(new JLabel("Enemy Waters", SwingConstants.CENTER), BorderLayout.NORTH);
        opponentGridUI = new GridUI(rows, cols);
        opponentPanel.add(opponentGridUI, BorderLayout.CENTER);

        gridsPanel.add(playerPanel);
        gridsPanel.add(opponentPanel);
        add(gridsPanel, BorderLayout.CENTER);
    }

    @Override
    public void setPlayerGridListener(GridInteractionObserver observer) {
        playerGridUI.setObserver(observer);
    }

    @Override
    public void setOpponentGridListener(GridInteractionObserver observer) {
        opponentGridUI.setObserver(observer);
    }

    @Override
    public void showGamePhase() {

    }

    @Override
    public void showEndGamePhase(String message) {
        systemMessage.setText(message);
    }

    @Override
    public void updatePlayerGrid(String gridSerialized, List<Ship> fleetToRender) {
        playerGridUI.displayData(gridSerialized, fleetToRender);
    }

    @Override
    public void updateOpponentGrid(String gridSerialized, List<Ship> fleetToRender) {
        opponentGridUI.displayData(gridSerialized, fleetToRender);
    }

    @Override
    public void showSystemMessage(String message) {

    }

    public void setPlayerTurn(boolean isPlayerTurn){

    }

    @Override
    public void showShotPreview(Coordinate coord) {
        opponentGridUI.showPlacementPreview(java.util.Set.of(coord), true, null);
    }

    @Override
    public void playerErrorSound() {
        Toolkit.getDefaultToolkit().beep();
    }

    public void showStatusMessage(String message){
        systemMessage.setText(message);
    }
}