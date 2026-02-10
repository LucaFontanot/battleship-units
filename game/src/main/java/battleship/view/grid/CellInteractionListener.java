package battleship.view.grid;

import it.units.battleship.Coordinate;

public interface CellInteractionListener {
    void onCellHover(Coordinate coordinate);

    void onCellExit();

    void onCellClicked(Coordinate coordinate);
}

