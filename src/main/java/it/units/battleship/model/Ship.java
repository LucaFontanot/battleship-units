package it.units.battleship.model;

import lombok.Getter;
import lombok.NonNull;

import java.util.*;

public abstract class Ship {
    @Getter
    private final Set<Coordinate> coordinates;
    @Getter
    private final Set<Coordinate> hitCoordinates;

    public Ship(@NonNull Set<Coordinate> coordinates){
        if (coordinates.size() <= 1){
            throw new IllegalArgumentException("Ship must have at least two cells");
        }
        this.coordinates = Collections.unmodifiableSet(new HashSet<>(coordinates));
        this.hitCoordinates = new HashSet<>();
    }

    /*
     * Adds a hit to the ship.
     * @param coordinate the coordinate of the cell that has been hit
     * @throws IllegalArgumentException if the specified coordinate is not part of the ship
     * @return true if the hit was successfully added, false otherwise
     */
    public boolean addHit(@NonNull Coordinate coordinate){
        if (!coordinates.contains(coordinate)){
            throw new IllegalArgumentException("Coordinate is not part of the ship");
        }
        if (hitCoordinates.contains(coordinate)){
            return false;
        }
        return hitCoordinates.add(coordinate);
    }
}
