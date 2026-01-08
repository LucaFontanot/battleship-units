import battleship.model.CellStates;
import it.units.battleship.Coordinate;
import battleship.model.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestGrid {
    private Grid grid;

    @Test
    public void testGridCreation(){
        assertThrowsExactly(IllegalArgumentException.class, () -> new Grid(0, 5));
    }

    @Test
    public void testGridCreation_RectangulareDimensions(){
        Grid grid = new Grid(2, 3);
        CellStates emptyCell = CellStates.EMPTY;
        CellStates[][] expected = {
                {emptyCell, emptyCell, emptyCell},
                {emptyCell, emptyCell, emptyCell}
        };
        for(int row = 0; row < 2; row++){
            for (int col = 0; col < 3; col++){
                assertEquals(expected[row][col], grid.getState(new Coordinate(row, col)), "The grid is not initialized correctly.");
            }
        }

    }

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
        for(int row = 0; row < 5; row++){
            for (int col = 0; col < 5; col++){
                assertEquals(expected[row][col], grid.getState(new Coordinate(row, col)), "The grid is not initialized correctly.");
            }
        }
    }

    @Test
    public void testChangeGridCellState(){
        grid.changeState(new Coordinate(2,2), CellStates.HIT);
        assertEquals(CellStates.HIT, grid.getState(new Coordinate(2,2)), "The grid cell state is not updated correctly.");
    }

    @Test
    public void testChangeGridCellState_OutOfBounds(){
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> grid.changeState(new Coordinate(-1,-1), CellStates.HIT));
    }

    @Test
    public void testChangeGridCellStateOutOfBounds(){
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> grid.changeState(new Coordinate(5,5), CellStates.HIT));
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
        CellStates sunkCell = CellStates.SUNK;

        grid.changeState(new Coordinate(2,4), hitCell);
        grid.changeState(new Coordinate(3,1), missCell);
        grid.changeState(new Coordinate(4,3), sunkCell);
        grid.changeState(new Coordinate(4,4), hitCell);

        String expected = "00000000000000X0M000000KX";

        assertEquals(expected, grid.gridSerialization(), "The grid is not serialized correctly.");
    }

    @Test
    public void testEmptyGridSerialization() {
        String expectedSerialization = "0000000000000000000000000";
        assertEquals(expectedSerialization, grid.gridSerialization(), "Empty grid serialization failed.");
    }

    @Test
    public void testFullGridSerialization() {
        Grid grid = new Grid(2, 2);
        grid.changeState(new Coordinate(0, 0), CellStates.HIT);
        grid.changeState(new Coordinate(0, 1), CellStates.SUNK);
        grid.changeState(new Coordinate(1, 0), CellStates.MISS);
        grid.changeState(new Coordinate(1, 1), CellStates.HIT);
        String expectedSerialization = "XKMX";
        assertEquals(expectedSerialization, grid.gridSerialization(), "Full grid serialization failed.");
    }

    @Test
    public void testGridSerializationNoOverwrites() {
        grid.changeState(new Coordinate(0, 0), CellStates.HIT);
        grid.changeState(new Coordinate(0, 1), CellStates.HIT);
        grid.changeState(new Coordinate(0, 0), CellStates.EMPTY);
        String expectedSerialization = "0X00000000000000000000000";
        assertEquals(expectedSerialization, grid.gridSerialization(), "Serialization with a reverted cell state failed.");
    }
}
