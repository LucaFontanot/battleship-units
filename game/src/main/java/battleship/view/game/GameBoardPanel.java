package battleship.view.game;

import battleship.view.grid.GridUI;

import javax.swing.*;
import java.awt.*;

public class GameBoardPanel extends JPanel {

    public GameBoardPanel(GridUI playerGridUI, GridUI opponentGridUI) {
        setLayout(new GridLayout(1, 2, 24, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(wrapWithTitle("YOU", playerGridUI));
        add(wrapWithTitle("OPPONENT", opponentGridUI));
    }

    private JComponent wrapWithTitle(String title, JComponent content) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(label, BorderLayout.NORTH);

        JPanel contentWrap = new JPanel(new GridBagLayout());
        contentWrap.add(content); // keeps grid centered
        panel.add(contentWrap, BorderLayout.CENTER);

        return panel;
    }
}
