package battleship.ui;

import it.units.battleship.CellState;
import it.units.battleship.view.grid.CellPanel;
import it.units.battleship.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestCellPanel {

    @Test
    public void testInitialState() {
        Coordinate coord = new Coordinate(5, 5);
        CellPanel cell = new CellPanel(coord);

        Assertions.assertEquals(CellState.EMPTY, cell.getCurrentState(), "Cell should be initially EMPTY");
    }

    @Test
    public void testUpdateState() {
        Coordinate coord = new Coordinate(0, 0);
        CellPanel cell = new CellPanel(coord);

        cell.updateState(CellState.HIT);
        Assertions.assertEquals(CellState.HIT, cell.getCurrentState(), "State should update to HIT");

        cell.updateState(CellState.MISS);
        Assertions.assertEquals(CellState.MISS, cell.getCurrentState(), "State should update to MISS");
    }
}

