package battleship.controller;

import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;

public interface GridInteractionObserver {

    void onShipPlacement(Coordinate coordinate, ShipType shipType, Orientation orientation);

    void onShipPlacementExit();
}
