package battleship.view.grid;

import battleship.model.CellState;
import it.units.battleship.Coordinate;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * Represents a single interactive cell within the Battleship grid.
 *
 * Visually, it can display a background color corresponding to its state (Hit, Miss, Empty)
 * and an optional texture overlay (e.g., a part of a ship).
 * Functionally, it acts as an input source, capturing mouse clicks and notifying
 * registered listeners with its.
 *
 */
public class CellPanel extends JLabel {

    static final double OPACITY = 0.5;
    static final Color HIT_COLOR = new Color(0xFF8000);
    static final Color MISS_COLOR = new Color(0x0000FF);
    static final Color EMPTY_COLOR = new Color(0xFFFFFF);
    static final Color SUNK_COLOR = new Color(0xFF0303);

    private CellState currentState;
    final Coordinate coordinate;
    private Color baseColor; // colore base della cella

    public CellPanel(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.currentState = CellState.EMPTY;
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
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            Dimension preferred = getPreferredSize();
            w = preferred.width;
            h = preferred.height;
        }
        setIcon(new ImageIcon(texture.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
    }

    public void removeTexture() {
        setIcon(null);
    }

    public void updateState(CellState newState){
        this.currentState = newState;
        refresh();
    }

    public void refresh() {
        switch (currentState) {
            case EMPTY -> baseColor = EMPTY_COLOR;
            case HIT -> baseColor = HIT_COLOR;
            case MISS -> baseColor = MISS_COLOR;
            case SUNK -> baseColor = SUNK_COLOR;
            default -> baseColor = EMPTY_COLOR;
        }
        setBackground(applyOpacity(baseColor, OPACITY));
        repaint();
    }

    public CellState getCurrentState() {
        return currentState;
    }
}