package battleship.controller.game.actions;

import it.units.battleship.Coordinate;

public interface GridInteractionObserver {

    void onGridHover(Coordinate coordinate);
    void onGridClick(Coordinate coordinate);
}
