package battleship.controller.handlers.ui;

import battleship.controller.actions.GameInteractionFacade;
import battleship.controller.actions.GridInteractionObserver;
import it.units.battleship.Coordinate;
import lombok.NonNull;

public class OpponentGridHandler implements GridInteractionObserver {

    public final GameInteractionFacade actions;

    public OpponentGridHandler(@NonNull GameInteractionFacade actions){
        this.actions = actions;
    }

    @Override
    public void onGridHover(Coordinate coordinate) {
        actions.previewShot(coordinate);
    }

    @Override
    public void onGridClick(Coordinate coordinate) {
        actions.requestShot(coordinate);
    }
}
