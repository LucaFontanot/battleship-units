package battleship.ui.grid;

import battleship.model.CellState;
import battleship.model.Grid;
import it.units.battleship.Coordinate;

import javax.swing.*;
import java.awt.*;

public class CellPanel extends JLabel {

    static final double OPACITY = 0.5;
    //Use HEX colors for better clarity
    static final Color HIT_COLOR = new Color(0xFF8000);
    static final Color MISS_COLOR = new Color(0x0000FF);
    static final Color EMPTY_COLOR = new Color(0xFFFFFF);
    static final Color SUNK_COLOR = new Color(0xFF0303);

    final Grid grid;
    final Coordinate coordinate;

    public CellPanel(Grid grid, Coordinate coordinate) {
        this.grid = grid;
        this.coordinate = coordinate;
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // bordo per la cella
        updateState();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(40, 40);
    }

    public void updateState() {
        CellState state = grid.getState(coordinate);
        switch (state) {
            case EMPTY -> {
                setBackground(EMPTY_COLOR);
            }
            case HIT -> {
                setBackground(HIT_COLOR);
            }
            case MISS -> {
                setBackground(MISS_COLOR);
            }
            case SUNK -> {
                setBackground(SUNK_COLOR);
            }
        }
        setBackground(new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), (int)(OPACITY * 255)));
    }
}