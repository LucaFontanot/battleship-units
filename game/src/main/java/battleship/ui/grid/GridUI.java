package battleship.ui.grid;

import battleship.model.FleetManager;
import battleship.model.Grid;
import battleship.model.Ship;
import battleship.ui.TextureLoader;
import battleship.ui.setup.PlacementContext;
import it.units.battleship.Coordinate;
import it.units.battleship.Logger;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Set;

public class GridUI extends JPanel implements CellHoverListener {
    final Grid grid;
    final FleetManager fleetManager;
    @Getter
    final int cols;
    @Getter
    final int rows;
    private CellPanel[][] cells;
    private final PlacementContext placementContext;

    public GridUI(FleetManager fleetManager, PlacementContext placementContext, CellClickListener clickListener) {
        this.grid = fleetManager.getGrid();
        this.fleetManager = fleetManager;
        this.placementContext = placementContext;

        this.cols = grid.getCol();
        this.rows = grid.getRow();

        setLayout(new GridLayout(rows, cols, 0, 0));
        cells = new CellPanel[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new CellPanel(grid, new Coordinate(r, c));
                cells[r][c].setHoverListener(this);
                cells[r][c].setClickListener(clickListener);


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

    public void clearPlacementPreview() {
        for (CellPanel[] row : cells) {
            for (CellPanel cell : row) {
                cell.setPreview(false, true);
            }
        }
    }


    public void showPlacementPreview(Set<Coordinate> coords, boolean valid) {
        clearPlacementPreview();
        for (Coordinate c : coords) {
            if (c.row() >= 0 && c.row() < rows && c.col() >= 0 && c.col() < cols) {
                cells[c.row()][c.col()].setPreview(true, valid);
            }
        }
    }

    @Override
    public void onCellHover(Coordinate coordinate) {
        clearPlacementPreview();

        if (placementContext == null) return;
        if (placementContext.getSelectedShipType() == null) return;

        var shipType = placementContext.getSelectedShipType();
        var orientation = placementContext.getSelectedOrientation();

        try {
            Ship ship = Ship.createShip(coordinate, orientation, shipType, fleetManager.getGrid());
            boolean valid = fleetManager.canPlaceShip(ship);

            showPlacementPreview(ship.getCoordinates(), valid);

        } catch (IllegalArgumentException ex) {
            var coords = shipType.getShipCoordinates(coordinate, orientation);
            showPlacementPreview(coords, false);
        }
    }

    @Override
    public void onCellExit() {
        clearPlacementPreview();
    }

    public void markSelected(Set<Coordinate> coords) {
        for (Coordinate c : coords) {
            if (c.row() >= 0 && c.row() < rows && c.col() >= 0 && c.col() < cols) {
                cells[c.row()][c.col()].setSelected(true);
            }
        }
    }

    public void clearSelected() {
        for (CellPanel[] row : cells) {
            for (CellPanel cell : row) {
                cell.setSelected(false);
            }
        }
    }
}
