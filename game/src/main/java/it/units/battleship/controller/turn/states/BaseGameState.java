package it.units.battleship.controller.turn.states;

import it.units.battleship.controller.turn.GameContext;
import it.units.battleship.controller.turn.TurnState;
import it.units.battleship.controller.turn.contracts.NetworkActions;
import it.units.battleship.controller.turn.contracts.StateTransitions;
import it.units.battleship.controller.turn.contracts.ViewActions;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
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

    protected final Grid opponentGrid;
    protected final ViewActions view;
    protected final StateTransitions stateTransitions;
    protected final NetworkActions network;
    protected final FleetManager fleetManager;


    protected BaseGameState(GameContext ctx){
        this.view = ctx.view();
        this.stateTransitions = ctx.transitions();
        this.network = ctx.network();
        this.fleetManager = ctx.fleetManager();
        this.opponentGrid = ctx.opponentGrid();
    }

    @Override
    public void onEnter() {
        Logger.debug("Entering state: " + getStateName());
        view.refreshPlayerGrid();
        view.refreshFleetUI();
    }

    @Override
    public void onExit() {
        Logger.debug("Exiting state: " + getStateName());
    }

    @Override
    public void handleOpponentGridClick( Coordinate coordinate) {
    }

    @Override
    public void handlePlayerGridClick( Coordinate coordinate) {
    }

    @Override
    public void handleOpponentGridHover( Coordinate coordinate) {
    }

    @Override
    public void handlePlayerGridHover( Coordinate coordinate) {
    }

    @Override
    public void handleIncomingShot( Coordinate coordinate) {
    }

    @Override
    public void handleOpponentGridUpdate( String grid, List<Ship> fleet) {
        view.updateOpponentGrid(grid, fleet);
    }

    @Override
    public void handleGameStatusReceived( GameState state) {
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
