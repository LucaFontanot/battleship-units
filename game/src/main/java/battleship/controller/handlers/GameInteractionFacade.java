package battleship.controller.handlers;

import it.units.battleship.Coordinate;

public interface GameInteractionFacade {
    void requestShot(Coordinate coordinate);
    void requestShipPlacement(Coordinate coordinate);
    void requestPlacementPreview(Coordinate coordinate);
    void previewShot(Coordinate coordinate);
}
