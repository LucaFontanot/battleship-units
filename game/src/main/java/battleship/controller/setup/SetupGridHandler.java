package battleship.controller.setup;

import battleship.controller.actions.GameInteractionFacade;
import battleship.controller.actions.GridInteractionObserver;
import it.units.battleship.Coordinate;
import lombok.NonNull;

public class SetupGridHandler implements GridInteractionObserver {

    private final SetupInteractionFacade actions;

    public SetupGridHandler(@NonNull SetupInteractionFacade actions) {
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
