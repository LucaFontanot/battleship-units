package it.units.battleship.model;

import it.units.battleship.model.ShipType;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;
/**
 * Represents the base implementation of a ship in the Battleship game.
 * The ship is characterized by its type, a set of coordinates it occupies on the grid,
 * and a record of the coordinates that have been hit.
 * It supports operations such as registering hits, checking if the ship is sunk,
 * and retrieving various attributes of the ship.
 */
public class StandardShip implements IShip {

    private final ShipType type;
    @Getter
    private final Set<Coordinate> coordinates;
    @Getter
    private final Set<Coordinate> hitCoordinates;

    public StandardShip(@NonNull Set<Coordinate> coordinates, @NonNull ShipType type){
        if (coordinates.size() != type.getSize()){
            throw new IllegalArgumentException("Ship must have the same number of cells as its type specifies: " + type.getName());
        }
        this.type = type;
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
     * Checks if the ship has been completely sunk.
     * A ship is considered sunk if the number of hits recorded on it equals its size.
     *
     * @return true if the ship is sunk (all cells have been hit), false otherwise
     */
    public boolean isSunk(){
        return getSize() == getHitsCount();
    }

    /**
     * Retrieves the size of the ship.
     *
     * @return the number of cells that compose the ship
     */
    public int getSize(){
        return type.getSize();
    }

    /**
     * Retrieves the name of the ship.
     *
     * @return the name of the ship as a string
     */
    public String getName(){
        return type.getName();
    }

    /**
     * Retrieves the number of hits registered on the ship.
     *
     * @return the count of cells that have been hit on the ship
     */
    public int getHitsCount(){
        return hitCoordinates.size();
    }


    /**
     * Retrieves the type of the ship.
     *
     * @return the type of the ship as an instance of ShipType
     */
    public ShipType getShipType(){
        return this.type;
    }

}
