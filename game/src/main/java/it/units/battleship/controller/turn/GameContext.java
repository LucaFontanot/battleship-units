package it.units.battleship.controller.turn;

import it.units.battleship.controller.turn.contracts.NetworkActions;
import it.units.battleship.controller.turn.contracts.StateTransitions;
import it.units.battleship.controller.turn.contracts.ViewActions;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;

public record GameContext(
        ViewActions view,
        StateTransitions transitions,
        NetworkActions network,
        FleetManager fleetManager,
        Grid opponentGrid
) { }
