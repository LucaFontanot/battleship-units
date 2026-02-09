package battleship.view.game;

import battleship.controller.game.actions.GridInteractionObserver;
import battleship.model.game.Ship;
import battleship.view.grid.GridUI;
import it.units.battleship.Coordinate;
import it.units.battleship.ShipType;
import lombok.Getter;

import javax.swing.*;
import java.util.List;

import java.awt.*;
import java.util.Map;

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
public class GameFrame extends JFrame implements GameView {
    @Getter
    private final GridUI playerGridUI;
    @Getter
    private final GridUI opponentGridUI;

    private JPanel currentPanel;
    private GridInteractionObserver observer;
    private boolean gameOver = false;

    private final JLabel systemMessage = new JLabel(" ");

    public GameFrame() {
        this.playerGridUI = new GridUI(10,10);
        this.opponentGridUI = new GridUI(10,10);

        setTitle("Battleship");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        systemMessage.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));
        systemMessage.setVerticalAlignment(SwingConstants.CENTER);

        add(systemMessage, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void updatePlayerGrid(String gridSerialized, List<Ship> fleetToRender) {
        this.playerGridUI.displayData(gridSerialized, fleetToRender);
    }

    @Override
    public void updateOpponentGrid(String gridSerialized, List<Ship> fleetToRender) {
        this.opponentGridUI.displayData(gridSerialized, fleetToRender);
    }

    @Override
    public void showSystemMessage(String message) {
        systemMessage.setText(message != null ? message : " ");
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
    public void refreshFleetSelection(Map<ShipType, Integer> shipCounts, Map<ShipType, Integer> fleetConfiguration) {

    }

    @Override
    public void open() {
        setVisible(true);
    }

    @Override
    public void showGamePhase() {
        systemMessage.setText("Game started.");
        switchPanel(new GameBoardPanel(playerGridUI, opponentGridUI));
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void showEndGamePhase(String message) {
        gameOver = true;
        opponentGridUI.clearPlacementPreview();

        showSystemMessage(message != null ? message : "Game Over");
    }

    @Override
    public void displayErrorAlert(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void setPlayerTurn(boolean isPlayerTurn) {
        opponentGridUI.setEnabled(isPlayerTurn);

        if (!isPlayerTurn) {
            opponentGridUI.clearPlacementPreview();
            showSystemMessage("Waiting for opponent...");
        } else {
            showSystemMessage("Your turn!");
        }
    }

    @Override
    public void showShotPreview(Coordinate coord) {
        opponentGridUI.clearPlacementPreview();
        opponentGridUI.getCellAt(coord.row(), coord.col()).setPreview(true, true);
    }

    @Override
    public void playerErrorSound() {
        Toolkit.getDefaultToolkit().beep();
    }

    private void switchPanel(JPanel panel) {
        getContentPane().removeAll();
        add(systemMessage, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        currentPanel = panel;
        revalidate();
        repaint();
    }
}