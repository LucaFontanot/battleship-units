package battleship.ui.setup;

import battleship.model.Orientation;
import battleship.model.ShipType;

public interface PlacementContext {
    ShipType getSelectedShipType();
    Orientation getSelectedOrientation();
}

