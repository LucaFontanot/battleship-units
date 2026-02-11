package it.units.battleship.view.core.setup;


import it.units.battleship.Orientation;
import it.units.battleship.ShipType;

public interface PlacementContext {
    ShipType getSelectedShipType();

    Orientation getSelectedOrientation();
}

