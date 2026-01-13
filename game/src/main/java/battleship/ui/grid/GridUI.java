package battleship.ui.grid;

import battleship.model.Grid;
import it.units.battleship.Coordinate;

import javax.swing.*;
import java.awt.*;

public class GridUI extends JPanel {
    final Grid grid;
    final int cols;
    final int rows;
    private CellPanel[][] cells;

    public GridUI(Grid grid) {
        this.grid = grid;
        this.cols = grid.getCol();
        this.rows = grid.getRow();
        setLayout(new GridLayout(rows, cols, 1, 1));
        cells = new CellPanel[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new CellPanel(grid, new Coordinate(r,c));
                add(cells[r][c]);
            }
        }
    }

    public void reload() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c].updateState();
            }
        }
        revalidate();
        repaint();
    }

}
