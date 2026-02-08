package battleship.controller.turn.states;

import battleship.controller.turn.TurnManager;
import battleship.controller.turn.TurnState;
import battleship.model.FleetManager;
import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.GridMapper;

import java.util.List;

public class WaitingOpponentState implements TurnState {
    @Override
    public void onEnter(TurnManager manager) {
        TurnState.super.onEnter(manager);
        manager.getView().showGamePhase();
        manager.getView().setPlayerTurn(false);
    }

    @Override
    public void handleOpponentGridUpdate(TurnManager manager, String grid, List<Ship> fleet){
        TurnState.super.handleOpponentGridUpdate(manager, grid, fleet);
    }

    @Override
    public void handleIncomingShot(TurnManager manager, Coordinate coordinate) {
        FleetManager fleetManager = manager.getFleetManager();

        boolean hit = fleetManager.handleIncomingShot(coordinate);

        manager.getGameModeStrategy().sendGridUpdate(
                fleetManager.getGrid(),
                fleetManager.getFleet(),
                hit
        );

        String gridSerialized = GridMapper.serialize(fleetManager.getGrid().getGrid());
        manager.getView().updatePlayerGrid(gridSerialized, fleetManager.getFleet());

        if (fleetManager.isGameOver()) {
            manager.transitionTo(new GameOverState(false, "You lost! All your ships are sunk."));
        } else {
            manager.transitionTo(new ActiveTurnState());
        }
    }

    @Override
    public String getStateName() {
        return GameState.WAITING_FOR_OPPONENT.name();
    }
}
