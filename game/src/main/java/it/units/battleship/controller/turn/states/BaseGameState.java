package it.units.battleship.controller.turn.states;

import it.units.battleship.controller.turn.GameActions;
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
    public void onEnter(GameActions actions) {
        Logger.debug("Entering state: " + getStateName());
        actions.refreshFleetUI();
    }

    @Override
    public void onExit() {
        Logger.debug("Exiting state: " + getStateName());
    }

    @Override
    public void handleOpponentGridClick(GameActions actions, Coordinate coordinate) {
    }

    @Override
    public void handlePlayerGridClick(GameActions actions, Coordinate coordinate) {
    }

    @Override
    public void handleOpponentGridHover(GameActions actions, Coordinate coordinate) {
    }

    @Override
    public void handlePlayerGridHover(GameActions actions, Coordinate coordinate) {
    }

    @Override
    public void handleIncomingShot(GameActions actions, Coordinate coordinate) {
    }

    @Override
    public void handleOpponentGridUpdate(GameActions actions, String grid, List<Ship> fleet) {
        actions.updateOpponentGrid(grid, fleet);
    }

    @Override
    public void handleGameStatusReceived(GameActions actions, GameState state) {
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
