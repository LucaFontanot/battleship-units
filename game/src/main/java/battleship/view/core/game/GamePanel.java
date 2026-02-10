package battleship.view.core.game;

import battleship.controller.game.actions.GridInteractionObserver;
import battleship.model.Ship;
import battleship.view.grid.GridUI;
import it.units.battleship.Coordinate;
import lombok.Getter;
import lombok.Setter;

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

    private final JLabel turnIndicator = new JLabel(" ");
    private final JLabel systemMessage = new JLabel(" ");

    private final JButton returnToMenuBtn = new JButton("Return to menu");
    @Setter
    private Runnable returnToMenuAction;
    private final JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    public GamePanel(int rows, int cols) {
        setLayout(new BorderLayout(10, 10));

        playerGridUI = new GridUI(rows, cols);
        opponentGridUI = new GridUI(rows, cols);

        // Status bar at top
        JPanel topPanel = new JPanel(new GridLayout(2,1));
        turnIndicator.setHorizontalAlignment(SwingConstants.HORIZONTAL);
        turnIndicator.setFont(new Font("Arial", Font.BOLD, 16));
        systemMessage.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(turnIndicator);
        topPanel.add(systemMessage);
        add(topPanel, BorderLayout.NORTH);

        // Grids panel
        JPanel gridsPanel = new JPanel(new GridLayout(1, 2, 24, 0));
        gridsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        gridsPanel.add(wrapWithTitle("YOU", playerGridUI));
        gridsPanel.add(wrapWithTitle("OPPONENT", opponentGridUI));
        add(gridsPanel, BorderLayout.CENTER);

        returnToMenuBtn.setVisible(false);
        returnToMenuBtn.addActionListener(e -> {
            if (returnToMenuAction != null) returnToMenuAction.run();
        });

        bottomBar.add(returnToMenuBtn);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private JComponent wrapWithTitle(String title, JComponent content) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(label, BorderLayout.NORTH);

        JPanel contentWrap = new JPanel(new GridBagLayout());
        contentWrap.add(content); // keeps grid centered
        panel.add(contentWrap, BorderLayout.CENTER);

        return panel;
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
        setReturnToMenuVisible(false);
        showSystemMessage("Game started.");
        turnIndicator.setText("Wait");
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
        systemMessage.setText(message != null ? message : " ");
    }

    public void setPlayerTurn(boolean isPlayerTurn){
        opponentGridUI.setEnabled(isPlayerTurn);
        if (isPlayerTurn){
            turnIndicator.setText("Your turn!");
            showSystemMessage("Select a target on the enemy grid.");
        }else {
            opponentGridUI.clearPlacementPreview();
            turnIndicator.setText("Opponent's turn");
            showSystemMessage("Waiting for opponent...");
        }
    }

    @Override
    public void showShotPreview(Coordinate coord) {
        opponentGridUI.clearPlacementPreview();
        opponentGridUI.getCellAt(coord.row(), coord.col()).setPreview(true, true);
    }

    @Override
    public void setReturnToMenuVisible(boolean visible) {
        returnToMenuBtn.setVisible(visible);
        bottomBar.setVisible(visible);
    }

    @Override
    public void playerErrorSound() {
        Toolkit.getDefaultToolkit().beep();
    }

    public void showStatusMessage(String message){
        systemMessage.setText(message);
    }
}