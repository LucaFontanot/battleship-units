package battleship.model;

import lombok.Getter;
import lombok.NonNull;

import java.util.*;
/**
 * Represents a ship in a Battleship game. A ship has a type, a set of coordinates indicating
 * its location on the grid, and a set of hit coordinates indicating which parts of the ship
 * have been hit.
 */
public class Ship {

    @Getter
    private final ShipType shipType;
    @Getter
    private final Set<Coordinate> coordinates;
    @Getter
    private final Set<Coordinate> hitCoordinates;

    /**
     * Creates a new ship on the grid with the specified initial coordinate, orientation, and type.
     * Validates that the ship's coordinates are within the grid boundaries.
     *
     * @param initCoordinate the starting coordinate from which the ship will be placed
     * @param orientation the orientation of the ship (e.g., horizontal or vertical)
     * @param type the type of the ship, which defines its size and shape
     * @param grid the grid on which the ship is to be placed
     * @return a new {@code Ship} instance positioned within the grid
     * @throws IllegalArgumentException if any part of the ship exceeds the boundaries of the grid
     */
    public static Ship createShip(@NonNull Coordinate initCoordinate,
                                  @NonNull Orientation orientation,
                                  @NonNull ShipType type,
                                  @NonNull Grid grid){
        Set<Coordinate> shipCoordinates = type.getShipCoordinates(initCoordinate, orientation);

        for (Coordinate coordinate : shipCoordinates) {
            if (coordinate.row() < 0 || coordinate.row() >= grid.getRow() || coordinate.col() < 0 || coordinate.col() >= grid.getCol()){
                throw new IllegalArgumentException("The ship coordinates must respect the grid dimension");
            }
        }

        return new Ship(shipCoordinates, type);
    }

    private Ship(@NonNull Set<Coordinate> coordinates, @NonNull ShipType type){
        if (coordinates.size() != type.getSize()){
            throw new IllegalArgumentException("Ship must have the same number of cells as its type specifies: " + type.getName());
        }
        this.shipType = type;
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
        return shipType.getSize();
    }

    /**
     * Retrieves the number of hits registered on the ship.
     *
     * @return the count of cells that have been hit on the ship
     */
    public int getHitsCount(){
        return hitCoordinates.size();
    }

}
