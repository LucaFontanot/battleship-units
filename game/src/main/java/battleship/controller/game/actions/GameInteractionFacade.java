package battleship.controller.game.actions;

import it.units.battleship.Coordinate;

public interface GameInteractionFacade {
    void requestShot(Coordinate coordinate);
    void previewShot(Coordinate coordinate);
    void requestShipPlacement(Coordinate coordinate);
    void requestPlacementPreview(Coordinate coordinate);
}
