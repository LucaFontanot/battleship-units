package battleship.controller.actions;

import it.units.battleship.Coordinate;

public interface GameInteractionFacade {
    void requestShot(Coordinate coordinate);
    void previewShot(Coordinate coordinate);
}
