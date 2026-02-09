package battleship.view.game;

import battleship.controller.game.actions.GridInteractionObserver;
import battleship.controller.setup.SetupGridHandler;
import battleship.model.game.Ship;
import battleship.view.grid.GridUI;
import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import lombok.Getter;

import javax.swing.*;
import java.util.LinkedHashSet;
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
public class GameFrame extends JFrame {
    @Getter
    private final GridUI playerGridUI;
    @Getter
    private final GridUI opponentGridUI;

    private final JPanel statusLabel;
    private final JPanel turnIndicator;

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

    private void switchPanel(JPanel panel) {
        getContentPane().removeAll();
        add(systemMessage, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        currentPanel = panel;
        revalidate();
        repaint();
    }
}