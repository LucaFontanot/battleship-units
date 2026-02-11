package battleship.ui;

import it.units.battleship.*;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.view.grid.GridUI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class TestGridUI {

    @BeforeEach
    public void setup() {
        Logger.setDebugEnabled(true);
    }

    Orientation[] orientations = Orientation.values();
    ShipType[] shipTypes = ShipType.values();

    @Test
    public void testGridInitialization() {
        GridUI gridUI = new GridUI(10, 10);

        Assertions.assertDoesNotThrow(() -> gridUI.getCellAt(0, 0));
        Assertions.assertDoesNotThrow(() -> gridUI.getCellAt(9, 9));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> gridUI.getCellAt(10, 10));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> gridUI.getCellAt(-1, 0));
    }
    @Test
    public void testDisplayDataParsing() {
        GridUI gridUI = new GridUI(2, 2);

        String gridData = "X0MK"; 
        gridUI.displayData(gridData, Collections.emptyList());

        Assertions.assertEquals(CellState.HIT, gridUI.getCellAt(0, 0).getCurrentState(), "Should parse 'X' as HIT");
        Assertions.assertEquals(CellState.EMPTY, gridUI.getCellAt(0, 1).getCurrentState(), "Should parse '0' as EMPTY");
        Assertions.assertEquals(CellState.MISS, gridUI.getCellAt(1, 0).getCurrentState(), "Should parse 'M' as MISS");
        Assertions.assertEquals(CellState.SUNK, gridUI.getCellAt(1, 1).getCurrentState(), "Should parse 'K' as SUNK");
    }

    /**
     * Checks that rendering works for all ship types in all orientations without crashing.
     * This replaces manual visual tests by ensuring the texture loading and placement logic is robust.
     */
    @ParameterizedTest
    @EnumSource(ShipType.class)
    public void testShipRenderingRobustness(ShipType type) {
        int size = 15;
        GridUI gridUI = new GridUI(size, size);
        Grid grid = new Grid(size, size);
        String emptyGrid = "0".repeat(size * size);

        for (Orientation orientation : Orientation.values()) {
            Coordinate start = new Coordinate(7, 7); 
            Ship ship = Ship.createShip(start, orientation, type, grid);
            Assertions.assertDoesNotThrow(() -> gridUI.displayData(emptyGrid, List.of(ship)),
                    "Rendering failed for " + type + " oriented " + orientation);
        }
    }

    @Test
    public void testVisualUI() throws InterruptedException {
        Grid grid = new Grid(10, 10);

        List<Ship> ships = List.of(
                Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid),
                Ship.createShip(new Coordinate(2, 2), Orientation.VERTICAL_DOWN, ShipType.BATTLESHIP, grid),
                Ship.createShip(new Coordinate(4, 4), Orientation.HORIZONTAL_RIGHT, ShipType.CRUISER, grid),
                Ship.createShip(new Coordinate(7, 7), Orientation.VERTICAL_DOWN, ShipType.FRIGATE, grid),
                Ship.createShip(new Coordinate(0, 5), Orientation.HORIZONTAL_RIGHT, ShipType.CARRIER, grid)
        );

        GridUI gridUI = new GridUI(10, 10);
        for (Ship ship : ships) {
            gridUI.placeShip(ship);
        }
        JFrame frame = new javax.swing.JFrame("GridUI Test");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(gridUI);
        frame.pack();
        frame.setVisible(true);
        Thread.sleep(2000);
        frame.dispose();
    }

    @Test
    public void testOrientations() throws InterruptedException {
        Grid grid = new Grid(15, 15);
        GridUI gridUI = new GridUI(15, 15);
        JFrame frame = new javax.swing.JFrame("GridUI Test");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(gridUI);
        frame.pack();
        frame.setVisible(true);
        for (ShipType type : ShipType.values()) {
            for (Orientation orientation : Orientation.values()) {
                Coordinate start = new Coordinate(7, 7);
                Ship ship = Ship.createShip(start, orientation, type, grid);
                Assertions.assertDoesNotThrow(() -> gridUI.displayData("0".repeat(15 * 15), List.of(ship)),
                        "Failed to render " + type + " in orientation " + orientation);
                gridUI.placeShip(ship);
                Thread.sleep(1000);
                gridUI.removeShip(ship);
            }
        }
    }

}