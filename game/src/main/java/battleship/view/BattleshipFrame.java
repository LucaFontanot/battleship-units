package battleship.view;

import battleship.controller.game.actions.GridInteractionObserver;
import battleship.model.game.Ship;
import battleship.utils.DimensionsUtils;
import battleship.view.game.GamePanel;
import battleship.view.setup.SetupPanel;
import it.units.battleship.Coordinate;
import it.units.battleship.Logger;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BattleshipFrame extends JFrame implements BattleshipView {

    private static final String SETUP_CARD = "SETUP";
    private static final String GAME_CARD = "GAME";
    private static final String WAITING_CARD = "WAITING";

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private final SetupPanel setupPanel;
    private final GamePanel gamePanel;
    private JPanel waitingPanel;
    private JLabel waitingLabel;

    private boolean inGamePhase = false;

    public BattleshipFrame(int gridRows, int gridCols) {
        super("Battleship");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        setupPanel = new SetupPanel(gridRows, gridCols);
        gamePanel = new GamePanel(gridRows, gridCols);

        waitingPanel = new JPanel(new GridBagLayout());
        waitingLabel = new JLabel("Waiting for opponent...");
        waitingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        waitingPanel.add(waitingLabel);

        mainPanel.add(setupPanel, SETUP_CARD);
        mainPanel.add(gamePanel, GAME_CARD);
        mainPanel.add(waitingPanel, WAITING_CARD);

        setContentPane(mainPanel);
        setSize(DimensionsUtils.getScaledDimensions(900, 700));
        setLocationRelativeTo(null);

        cardLayout.show(mainPanel, SETUP_CARD);
    }

    // ===== Grid Listeners - Propagati ai pannelli =====

    @Override
    public void setPlayerGridListener(GridInteractionObserver observer) {
        setupPanel.setObserver(observer);
        gamePanel.setPlayerGridListener(observer);
    }

    @Override
    public void setOpponentGridListener(GridInteractionObserver observer) {
        gamePanel.setOpponentGridListener(observer);
    }

    // ===== Lifecycle =====

    @Override
    public void open() {
        setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    // ===== Setup Phase Methods =====

    @Override
    public ShipType getSelectedShipType() {
        return setupPanel.getSelectedShipType();
    }

    @Override
    public Orientation getSelectedOrientation() {
        return setupPanel.getSelectedOrientation();
    }

    @Override
    public void showPlacementPreview(Set<Coordinate> coordinates, boolean valid, Ship previewShip) {
        if (!inGamePhase) {
            setupPanel.getPlayerGridUI().showPlacementPreview(coordinates, valid, previewShip);
        }
    }

    @Override
    public void refreshFleetSelection(Map<ShipType, Integer> placedCounts, Map<ShipType, Integer> requiredCounts) {
        setupPanel.updateShipButtons(placedCounts, requiredCounts);
    }

    @Override
    public void updatePlayerGrid(String gridSerialized, List<Ship> fleet) {
        if (inGamePhase) {
            gamePanel.getPlayerGridUI().displayData(gridSerialized, fleet);
        } else {
            setupPanel.getPlayerGridUI().displayData(gridSerialized, fleet);
        }
    }

    // ===== Game Phase Methods =====

    @Override
    public void updateOpponentGrid(String gridSerialized, List<Ship> fleet) {
        gamePanel.getOpponentGridUI().displayData(gridSerialized, fleet);
    }

    @Override
    public void setPlayerTurn(boolean isPlayerTurn) {
        gamePanel.setPlayerTurn(isPlayerTurn);
    }

    @Override
    public void showShotPreview(Coordinate coordinate) {
        gamePanel.getOpponentGridUI().clearPlacementPreview();
        gamePanel.getOpponentGridUI().showPlacementPreview(Set.of(coordinate), true, null);
    }

    @Override
    public void showEndGamePhase(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE)
        );
    }

    @Override
    public void showSystemMessage(String message) {
        gamePanel.showStatusMessage(message);
    }

    @Override
    public void playerErrorSound() {
        Toolkit.getDefaultToolkit().beep();
    }

    // ===== Phase Transitions =====

    @Override
    public void transitionToGamePhase() {
        Logger.log("BattleshipFrame: Transitioning to game phase");
        inGamePhase = true;

        SwingUtilities.invokeLater(() -> {
            cardLayout.show(mainPanel, GAME_CARD);
            revalidate();
            repaint();
        });
    }

    @Override
    public void showWaitingForOpponent(String message) {
        SwingUtilities.invokeLater(() -> {
            waitingLabel.setText(message);
            cardLayout.show(mainPanel, WAITING_CARD);
        });
    }
}
