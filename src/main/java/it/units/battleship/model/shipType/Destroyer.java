package it.units.battleship.model.shipType;

import it.units.battleship.model.Coordinate;
import it.units.battleship.model.Ship;
import lombok.NonNull;

import java.util.Set;

public class Destroyer extends Ship {

    public Destroyer(@NonNull Set<Coordinate> coordinates, @NonNull ShipType type) {
        super(coordinates, type);
    }
}
