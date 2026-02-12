package it.units.battleship.controller.turn.contracts;

import it.units.battleship.Orientation;
import it.units.battleship.ShipType;

/**
 * Responsibility: Gets the data selected by the user during the setup phase.
 */
public interface SetupInputProvider{
    /** get which ship is select in UI */
    ShipType getSelectedShipType();
    /** get the current rotate orientation */
    Orientation getSelectedOrientation();
}
