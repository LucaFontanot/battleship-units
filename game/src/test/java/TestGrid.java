import battleship.model.CellStates;
import battleship.model.Coordinate;
import battleship.model.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGrid {
    private Grid grid;

    @BeforeEach
    void setUp() {
        grid = new Grid(5, 5);
    }

    @Test
    public void testInitGrid(){
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
        grid.changeState(new Coordinate(2,2), CellStates.HIT);
        assertEquals(CellStates.HIT, grid.getGrid()[2][2], "The grid cell state is not updated correctly.");
    }

    @Test
    public void testGetGridCellState(){
        grid.changeState(new Coordinate(2,1), CellStates.HIT);
        assertEquals(CellStates.HIT, grid.getState(new Coordinate(2,1)), "The grid cell state is not extracted correctly.");
    }

    @Test
    public void testGridSerialization(){

        CellStates hitCell = CellStates.HIT;
        CellStates missCell = CellStates.MISS;
        CellStates shipCell = CellStates.SHIP;

        grid.changeState(new Coordinate(2,4), shipCell);
        grid.changeState(new Coordinate(3,1), missCell);
        grid.changeState(new Coordinate(4,4), hitCell);

        String expected = "00000000000000S0M0000000X";

        assertEquals(expected, grid.gridSerialization(), "The grid is not serialized correctly.");
    }
}
