package battleship.ui.grid;

import it.units.battleship.Coordinate;

public interface CellHoverListener {
    void onCellHover(Coordinate coordinate);
    void onCellExit();
}

