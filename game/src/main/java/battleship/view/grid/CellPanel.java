package battleship.view.grid;

import battleship.model.CellState;
import battleship.model.Grid;
import it.units.battleship.Coordinate;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CellPanel extends JLabel {

    static final double OPACITY = 0.5;
    static final Color HIT_COLOR = new Color(0xFF8000);
    static final Color MISS_COLOR = new Color(0x0000FF);
    static final Color EMPTY_COLOR = new Color(0xFFFFFF);
    static final Color SUNK_COLOR = new Color(0xFF0303);

    final Grid grid;
    final Coordinate coordinate;
    private Color baseColor; // colore base della cella

    public CellPanel(Grid grid, Coordinate coordinate) {
        this.grid = grid;
        this.coordinate = coordinate;
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        setFocusable(true);
        refresh();

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {

            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {

            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(40, 40);
    }

    private Color applyOpacity(Color color, double opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(opacity * 255));
    }

    public void addTexture(BufferedImage texture) {
        setIcon(new ImageIcon(texture.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH)));
    }

    public void removeTexture() {
        setIcon(null);
    }

    public void refresh() {
        CellState state = grid.getState(coordinate);
        switch (state) {
            case EMPTY -> baseColor = EMPTY_COLOR;
            case HIT -> baseColor = HIT_COLOR;
            case MISS -> baseColor = MISS_COLOR;
            case SUNK -> baseColor = SUNK_COLOR;
            default -> baseColor = EMPTY_COLOR;
        }
        setBackground(applyOpacity(baseColor, OPACITY));
        repaint();
    }
}