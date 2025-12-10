import it.units.battleship.model.Carrier;
import it.units.battleship.model.CellStates;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
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
        CellStates cell = CellStates.EMPTY;
        CellStates[][] expected = {
                {cell,cell,cell,cell,cell},
                {cell,cell,cell,cell,cell},
                {cell,cell,cell,cell,cell},
                {cell,cell,cell,cell,cell},
                {cell,cell,cell,cell,cell}
        };
        assertTrue(Arrays.deepEquals(expected, grid.getGrid()), "The grid is not initialized correctly.");
    }
}
