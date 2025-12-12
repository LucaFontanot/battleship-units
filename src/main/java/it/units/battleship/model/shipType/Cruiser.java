package it.units.battleship.model.shipType;

import it.units.battleship.model.Coordinate;
import it.units.battleship.model.Ship;
import lombok.NonNull;

import java.util.Set;

public class Cruiser extends Ship {

    public Cruiser(@NonNull Set<Coordinate> coordinates, @NonNull ShipType type) {
        super(coordinates, type);
    }
}
