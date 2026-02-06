package battleship.view;

import battleship.controller.actions.GridInteractionObserver;
import battleship.model.Ship;
import battleship.view.grid.GridUI;
import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import lombok.Getter;

import javax.swing.*;
import java.util.LinkedHashSet;
import java.util.List;
import battleship.view.setup.SetupPanel;

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
public class GameFrame extends JFrame implements GameView{
    @Getter
    private final GridUI playerGridUI;
    @Getter
    private final GridUI opponentGridUI;

    private SetupPanel setupPanel;
    private JPanel currentPanel;
    private GridInteractionObserver observer;

    private final JLabel systemMessage = new JLabel(" ");

    public GameFrame() {
        this.playerGridUI = new GridUI(10,10,null,null);
        this.opponentGridUI = new GridUI(10,10,null,null);

        setTitle("Battleship");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        setupPanel = new SetupPanel();

        systemMessage.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));
        systemMessage.setVerticalAlignment(SwingConstants.CENTER);

        add(systemMessage, BorderLayout.NORTH);
        add(setupPanel, BorderLayout.CENTER);

        currentPanel = setupPanel;

        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void updatePlayerGrid(String gridSerialized, List<Ship> fleetToRender) {
        if (currentPanel instanceof SetupPanel setup) {
            setup.getGridUI().displayData(gridSerialized, fleetToRender);
        }
        this.playerGridUI.displayData(gridSerialized, fleetToRender);
    }

    @Override
    public void updateOpponentGrid(String gridSerialized, List<Ship> fleetToRender) {
        this.opponentGridUI.displayData(gridSerialized, fleetToRender);
    }

    @Override
    public void showSystemMessage(String message) {

    }

    @Override
    public void setPlayerGridListener(GridInteractionObserver observer) {
        playerGridUI.setObserver(observer);
        setupPanel.getGridUI().setObserver(observer);
    }

    @Override
    public void setOpponentGridListener(GridInteractionObserver observer) {
        opponentGridUI.setObserver(observer);
    }

    @Override
    public void refreshFleetSelection(Map<ShipType, Integer> shipCounts, Map<ShipType, Integer> fleetConfiguration) {
        setupPanel.updateShipButtons(shipCounts, fleetConfiguration);
    }

    @Override
    public Orientation getSelectedOrientation() {
        return setupPanel.getSelectedOrientation();
    }

    @Override
    public ShipType getSelectedShipType() {
        return setupPanel.getSelectedShipType();
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
    public void showEndGamePhase(String message) {

    }

    @Override
    public void displayErrorAlert(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void setPlayerTurn(boolean isPlayerTurn) {
    }

    @Override
    public void showPlacementPreview(LinkedHashSet<Coordinate> coord, boolean validShip, Ship ship) {
        if (currentPanel instanceof SetupPanel setup) {
            setup.getGridUI().showPlacementPreview(coord, validShip, ship);
        }
    }

    @Override
    public void showShotPreview(Coordinate coord) {

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