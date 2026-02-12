package it.units.battleship.controller.game.ui;

import it.units.battleship.controller.game.actions.GameInteractionFacade;
import it.units.battleship.controller.game.actions.GridInteractionObserver;
import it.units.battleship.Coordinate;
import lombok.NonNull;

public class OpponentGridHandler implements GridInteractionObserver {

    private final GameInteractionFacade actions;

    public OpponentGridHandler(@NonNull GameInteractionFacade actions) {
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
