package battleship.view.grid;

import battleship.controller.game.actions.GridInteractionObserver;
import battleship.model.game.Ship;
import battleship.utils.TextureLoader;
import it.units.battleship.CellState;
import it.units.battleship.Coordinate;
import it.units.battleship.GridMapper;
import it.units.battleship.Logger;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;

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
public class GridUI extends JPanel implements CellInteractionListener {
    @Getter
    final int cols;
    @Getter
    final int rows;
    private CellPanel[][] cells;

    @Setter
    private GridInteractionObserver observer;

    public GridUI(int rows,
                  int cols) {
        this.cols = cols;
        this.rows = rows;

        setLayout(new GridLayout(rows, cols, 0, 0));
        cells = new CellPanel[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Coordinate coord = new Coordinate(r,c);
                cells[r][c] = new CellPanel(coord);

                cells[r][c].setCellListener(this);

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

        CellState[][] states = GridMapper.deserialize(gridSerialized, rows, cols);
        for(int r=0; r<rows ; r++){
            for(int c=0; c<cols; c++){
                cells[r][c].updateState(states[r][c]);
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
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c].refresh();
            }
        }
        revalidate();
        repaint();
    }

    public void clearPlacementPreview() {
        for (CellPanel[] row : cells) {
            for (CellPanel cell : row) {
                cell.setPreview(false, true);
                cell.clearOverlayTexture();
            }
        }
    }

    public CellPanel getCellAt(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Invalid grid coordinates");
        }
        return cells[row][col];
    }

    public void showPlacementPreview(Set<Coordinate> coords, boolean valid, Ship previewShip) {
        clearPlacementPreview();

        for (Coordinate c : coords) {
            if (c.row() >= 0 && c.row() < rows && c.col() >= 0 && c.col() < cols) {
                cells[c.row()][c.col()].setPreview(true, valid);

                // overlay ship image (50%)
                if (previewShip != null) {
                    BufferedImage img = TextureLoader.getTextureForShip(previewShip, c);
                    if (img != null) {
                        cells[c.row()][c.col()].setOverlayTexture(img, 0.5f);
                    } else {
                        cells[c.row()][c.col()].clearOverlayTexture();
                    }
                }
            }
        }
    }

    @Override
    public void onCellHover(Coordinate coordinate) {
        clearPlacementPreview();
        if (observer == null) return;
        observer.onGridHover(coordinate);
    }

    @Override
    public void onCellExit() {
        clearPlacementPreview();
    }


    @Override
    public void onCellClicked(Coordinate coordinate) {
        Logger.debug("GridUI::onCellClicked - Coordinate: " + coordinate);
        if (observer == null) return;
        observer.onGridClick(coordinate);
    }

    public void markSelected(Set<Coordinate> coords) {
        for (Coordinate c : coords) {
            if (c.row() >= 0 && c.row() < rows && c.col() >= 0 && c.col() < cols) {
                cells[c.row()][c.col()].setSelected(true);
            }
        }
    }

    public void placeShip(Ship ship) {
        for (Coordinate c : ship.getCoordinates()) {
            if (c.row() < 0 || c.row() >= rows || c.col() < 0 || c.col() >= cols) continue;

            BufferedImage img = TextureLoader.getTextureForShip(ship, c);
            if (img != null) {
                cells[c.row()][c.col()].addTexture(img);
            }
            cells[c.row()][c.col()].setSelected(true);
        }
        revalidate();
        repaint();
    }


    public void clearSelected() {
        for (CellPanel[] row : cells) {
            for (CellPanel cell : row) {
                cell.setSelected(false);
            }
        }
    }
}
