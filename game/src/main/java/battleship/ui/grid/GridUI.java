package battleship.ui.grid;

import battleship.model.FleetManager;
import battleship.model.Grid;
import battleship.model.Ship;
import battleship.ui.TextureLoader;
import it.units.battleship.Coordinate;
import it.units.battleship.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GridUI extends JPanel {
    final Grid grid;
    final FleetManager fleetManager;
    final int cols;
    final int rows;
    private CellPanel[][] cells;

    public GridUI(FleetManager fleetManager) {
        this.grid = fleetManager.getGrid();
        this.fleetManager = fleetManager;
        this.cols = grid.getCol();
        this.rows = grid.getRow();
        setLayout(new GridLayout(rows, cols, 0, 0));
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
                cells[r][c].removeTexture();
            }
        }
        for (Ship ship : fleetManager.getFleet()) {
            if (ship.isSunk()){
                for (Coordinate coord : ship.getCoordinates()) {
                    BufferedImage image = TextureLoader.getTextureForShip(ship, coord);
                    if (image == null) {
                        Logger.warn("No texture found for ship at coordinate: " + coord);
                        continue;
                    }
                    cells[coord.row()][coord.col()].addTexture(image);
                }
            }
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c].refresh();
            }
        }
        revalidate();
        repaint();
    }

}
