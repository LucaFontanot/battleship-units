package battleship.controller.turn.states;

import battleship.controller.turn.TurnManager;
import battleship.controller.turn.TurnState;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;

public class ActiveTurnState implements TurnState {
    @Override
    public void onEnter(TurnManager manager) {
        TurnState.super.onEnter(manager);
        manager.getView().showGamePhase();
        manager.getView().setPlayerTurn(true);
    }

    @Override
    public void handleOpponentGridClick(TurnManager manager, Coordinate coordinate) {
        manager.getGameModeStrategy().sendShot(coordinate);
        manager.transitionTo(new WaitingOpponentState());
    }

    @Override
    public void handleOpponentGridHover(TurnManager manager, Coordinate coordinate) {
        manager.getView().showShotPreview(coordinate);
    }

    @Override
    public String getStateName() {
        return GameState.ACTIVE_TURN.name();
    }

    @Override
    public boolean canShoot() {
        return true;
    }
}
