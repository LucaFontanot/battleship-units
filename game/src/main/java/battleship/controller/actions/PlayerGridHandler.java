package battleship.controller.actions;

import battleship.controller.handlers.GameInteractionFacade;
import battleship.controller.handlers.GridInteractionObserver;
import it.units.battleship.Coordinate;
import lombok.NonNull;

public class PlayerGridHandler implements GridInteractionObserver {

    private final GameInteractionFacade actions;

    public PlayerGridHandler(@NonNull GameInteractionFacade actions) {
        this.actions = actions;
    }

    @Override
    public void onGridHover(Coordinate coordinate) {
        actions.requestPlacementPreview(coordinate);
    }

    @Override
    public void onGridClick(Coordinate coordinate) {
        actions.requestShipPlacement(coordinate);
    }
}
