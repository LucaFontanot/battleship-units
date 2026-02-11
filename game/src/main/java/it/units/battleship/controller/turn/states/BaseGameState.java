package it.units.battleship.controller.turn.states;

import it.units.battleship.controller.turn.TurnManager;
import it.units.battleship.controller.turn.TurnState;
import it.units.battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Logger;

import java.util.List;

/**
 * Base abstract class for turn states.
 * Provides default implementations for TurnState methods and common utility logic.
 */
public abstract class BaseGameState implements TurnState {

    @Override
    public void onEnter(TurnManager manager) {
        Logger.debug("Entering state: " + getStateName());
        manager.refreshUI();
    }

    @Override
    public void onExit(TurnManager manager) {
        Logger.debug("Exiting state: " + getStateName());
    }

    @Override
    public void handleOpponentGridClick(TurnManager manager, Coordinate coordinate) {
    }

    @Override
    public void handlePlayerGridClick(TurnManager manager, Coordinate coordinate) {
    }

    @Override
    public void handleOpponentGridHover(TurnManager manager, Coordinate coordinate) {
    }

    @Override
    public void handlePlayerGridHover(TurnManager manager, Coordinate coordinate) {
    }

    @Override
    public void handleIncomingShot(TurnManager manager, Coordinate coordinate) {
    }

    @Override
    public void handleOpponentGridUpdate(TurnManager manager, String grid, List<Ship> fleet) {
        manager.updateOpponentGrid(grid, fleet);
    }

    @Override
    public void handleGameStatusReceived(TurnManager manager, GameState state) {
    }

    @Override
    public boolean canShoot() {
        return false;
    }

    @Override
    public boolean canPlaceShip() {
        return false;
    }
}
