package battleship.view.grid;

import battleship.model.CellState;
import battleship.model.Ship;
import battleship.view.utils.TextureLoader;
import it.units.battleship.Coordinate;
import it.units.battleship.Logger;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * A specialized Swing component responsible for rendering a game grid.
 * It manages a collection of {@link CellPanel} components arranged in a 2D layout
 * and handles the complex logic of overlaying ship textures onto the grid cells.
 *
 * Key features:
 *  - Decodes serialized grid strings into visual cell states (HIT, MISS, etc.).
 *  - Calculates and applies the correct texture segments for ships based on their
 *     position, orientation, and type using {@link TextureLoader}.
 */
public class GridUI extends JPanel {
    final int cols;
    final int rows;
    private CellPanel[][] cells;

    public GridUI(int rows, int cols) {
        this.cols = cols;
        this.rows = rows;
        setLayout(new GridLayout(rows, cols, 0, 0));
        cells = new CellPanel[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Coordinate coord = new Coordinate(r,c);
                cells[r][c] = new CellPanel(coord);
                add(cells[r][c]);
            }
        }
    }

    public void displayData(@NonNull String gridSerialized, List<Ship> ships){
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c].removeTexture();
            }
        }

        int index = 0;
        for(int r=0; r<rows ; r++){
            for(int c=0; c<cols; c++){
                char code = gridSerialized.charAt(index);
                CellState state = decodeCharToState(code);
                cells[r][c].updateState(state);
                index = index + 1;
            }
        }

        for (Ship ship : ships) {
            for (Coordinate coord : ship.getCoordinates()) {
                BufferedImage image = TextureLoader.getTextureForShip(ship, coord);
                if (image == null) {
                    Logger.warn("No texture found for ship at coordinate: " + coord);
                    continue;
                }
                cells[coord.row()][coord.col()].addTexture(image);
            }
        }
        revalidate();
        repaint();
    }

    private CellState decodeCharToState(char code){
        return switch (code){
            case 'X' -> CellState.HIT;
            case 'K' -> CellState.SUNK;
            case 'M' -> CellState.MISS;
            default -> CellState.EMPTY;
        };
    }

    public CellPanel getCellAt(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Invalid grid coordinates");
        }
        return cells[row][col];
    }
}
