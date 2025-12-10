import it.units.battleship.model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGrid {
    @Test
    public void testInitGrid(){
        List<Ship> navy = new ArrayList<>();
        navy.add(new Carrier());
        Grid grid = new Grid(5, 5, navy);
        CellStates emptyCell = CellStates.EMPTY;
        CellStates[][] expected = {
                {emptyCell, emptyCell, emptyCell, emptyCell, emptyCell},
                {emptyCell, emptyCell, emptyCell, emptyCell, emptyCell},
                {emptyCell, emptyCell, emptyCell, emptyCell, emptyCell},
                {emptyCell, emptyCell, emptyCell, emptyCell, emptyCell},
                {emptyCell, emptyCell, emptyCell, emptyCell, emptyCell}
        };
        System.out.println(Arrays.deepToString(grid.getGrid()));
        assertTrue(Arrays.deepEquals(expected, grid.getGrid()), "The grid is not initialized correctly.");
    }

    @Test
    public void testChangeGridCellState(){
        List<Ship> navy = new ArrayList<>();
        navy.add(new Carrier());
        Grid grid = new Grid(5, 5, navy);
        grid.changeState(new Coordinate(2,2), CellStates.HIT);
        assertEquals(CellStates.HIT, grid.getGrid()[2][2], "The grid cell state is not updated correctly.");
    }

    @Test
    public void testGetGridCellState(){
        List<Ship> navy = new ArrayList<>();
        navy.add(new Carrier());
        Grid grid = new Grid(5, 5, navy);
        grid.changeState(new Coordinate(2,1), CellStates.HIT);
        assertEquals(CellStates.HIT, grid.getState(new Coordinate(2,1)), "The grid cell state is not extracted correctly.");
    }

    @Test
    public void testGridSerialization(){
        List<Ship> navy = new ArrayList<>();
        navy.add(new Carrier());
        Grid grid = new Grid(5, 5, navy);

        CellStates hitCell = CellStates.HIT;
        CellStates missCell = CellStates.MISS;

        grid.changeState(new Coordinate(2,4), hitCell);
        grid.changeState(new Coordinate(3,1), missCell);
        grid.changeState(new Coordinate(4,4), hitCell);

        String expected = "00000000000000X0M0000000X";

        assertEquals(expected, grid.gridSerialization(), "The grid is not serialized correctly.");
    }
}
