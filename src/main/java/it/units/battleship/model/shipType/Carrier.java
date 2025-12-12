package it.units.battleship.model.shipType;

import it.units.battleship.model.Coordinate;
import it.units.battleship.model.Ship;
import lombok.NonNull;

import java.util.Set;

public final class Carrier extends Ship {

    public Carrier(@NonNull Set<Coordinate> coordinates, @NonNull ShipType shipType) {
         super(coordinates, shipType);
    }
}
