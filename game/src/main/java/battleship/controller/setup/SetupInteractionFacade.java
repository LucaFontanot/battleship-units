package battleship.controller.setup;

import it.units.battleship.Coordinate;

public interface SetupInteractionFacade {
    void requestShipPlacement(Coordinate coordinate);
    void requestPlacementPreview(Coordinate coordinate);
}
