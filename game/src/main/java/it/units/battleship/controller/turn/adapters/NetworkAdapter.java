package it.units.battleship.controller.turn.adapters;

import it.units.battleship.Coordinate;
import it.units.battleship.controller.mode.GameModeStrategy;
import it.units.battleship.controller.turn.contracts.NetworkActions;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import lombok.NonNull;

import java.util.List;

public class NetworkAdapter implements NetworkActions {

    private final GameModeStrategy gameMode;

    public NetworkAdapter(@NonNull GameModeStrategy gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public void fireShot(Coordinate coordinate) {
        gameMode.sendShot(coordinate);
    }

    @Override
    public void sendGameOver(String message) {
        gameMode.sendGameOver(message);
    }

    @Override
    public void notifySetupComplete() {
        gameMode.notifySetupComplete();
    }

    @Override
    public void sendGridUpdate(Grid grid, List<Ship> fleet, boolean hit) {
        gameMode.sendGridUpdate(grid, fleet, hit);
    }
}
