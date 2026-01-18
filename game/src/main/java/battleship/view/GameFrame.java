package battleship.view;

import battleship.model.FleetManager;
import battleship.model.Ship;
import battleship.ui.grid.GridUI;
import battleship.ui.setup.SetupPanel;

import javax.swing.*;
import java.awt.*;
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


public class GameFrame extends JFrame implements GameView {
    private SetupPanel setupPanel;
    private JPanel currentPanel;

    private final JLabel systemMessage = new JLabel(" ");

    public GameFrame(FleetManager fleetManager) {
        setTitle("Battleship");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        setupPanel = new SetupPanel(fleetManager);

        add(systemMessage, BorderLayout.NORTH);
        add(setupPanel, BorderLayout.CENTER);

        currentPanel = setupPanel;

        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void open() {
        setVisible(true);
    }

    @Override
    public void showSetupPhase() {
        switchPanel(setupPanel);
        systemMessage.setText("Setup phase: place your ships.");
    }

    @Override
    public void showGamePhase() {
        systemMessage.setText("Game started.");
    }

    @Override
    public void updatePlayerGrid(String serializedGrid, List<Ship> fleet) {
        if (currentPanel instanceof SetupPanel setup) {
            setup.getGridUI().reload();
        }
    }

    @Override
    public void updateOpponentGrid(String serializedGrid) {
    }

    @Override
    public void updateSystemMessage(String message) {
        systemMessage.setText(message);
    }

    @Override
    public void displayErrorAlert(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showEndGamePhase(String winner) {
        JOptionPane.showMessageDialog(this, "Winner: " + winner);
    }

    @Override
    public void displayShipSunk(Ship ship) {
        JOptionPane.showMessageDialog(this, "Ship sunk: " + ship.getShipType());
    }

    @Override
    public void setPlayerTurn(boolean isPlayerTurn) {
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
