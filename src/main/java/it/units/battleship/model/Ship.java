package it.units.battleship.model;

import lombok.Getter;
import lombok.NonNull;

import java.util.*;
/**
 * Represents a ship in the Battleship game.
 * Each ship is composed of a set of cells, and can be sunk if all its cells have been hit.
 *
 */
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

    /**
     * Adds a hit to the ship.
     * @param coordinate the coordinate of the cell that has been hit
     * @throws IllegalArgumentException if the specified coordinate is not part of the ship
     * @return true if the hit was successfully added, false otherwise
     */
    public boolean addHit(@NonNull Coordinate coordinate){
        if (hitCoordinates.contains(coordinate) || !coordinates.contains(coordinate)){
            return false;
        }
        return hitCoordinates.add(coordinate);
    }

    /**
     * Returns true if the ship is sunk, false otherwise.
     */
    public boolean isSunk(){
        return getSize() == getHitsCount();
    }

    /**
     * Returns the size of the ship.
     */
    public int getSize(){
        return coordinates.size();
    }

    /**
     * Returns the number of hits the ship has received.
     */
    public int getHitsCount(){
        return hitCoordinates.size();
    }

}
