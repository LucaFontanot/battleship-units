package ui;

import battleship.model.CellState;
import battleship.model.Grid;
import battleship.model.Ship;
import battleship.view.grid.GridUI;
import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Collections;
import java.util.List;

public class TestGridUI {

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
}