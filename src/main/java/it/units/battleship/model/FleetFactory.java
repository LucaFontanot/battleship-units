package it.units.battleship.model;

import it.units.battleship.model.shipType.*;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Factory class responsible for creating ships and associating them with specific coordinates
 * on a grid-based representation for a fleet-based game. The class ensures ships are placed
 * within grid boundaries and that their orientation and size are properly managed.
 */
public class FleetFactory {

    private final Grid grid;
    private final FleetManager manager = new FleetManager();

    public FleetFactory(Grid grid){
        this.grid = grid;
    }

    /**
     * Creates a ship of the specified type, orientation, and initial coordinates.
     * Validates the ship's coordinates to ensure they fit within the grid and constructs
     * the appropriate ship type object based on input.
     *
     * @param shipType the type of the ship to be created, such as CARRIER, CRUISER, etc.
     * @param orientation the orientation for the ship, such as HORIZONTAL_RIGHT, VERTICAL_DOWN, etc.
     * @param init_coordinate the starting coordinate for the ship placement on the grid
     * @throws IllegalArgumentException if the ship's coordinates exceed the boundaries of the grid or if the ship type is not supported
     * @return a new Ship instance of the specified type with the calculated coordinates
     */
    public Ship createShip(@NonNull ShipType shipType,
                           @NonNull Orientation orientation,
                           @NonNull Coordinate init_coordinate) throws IllegalArgumentException {
        List<Coordinate> shipCoordinates = computeShipCoordinates(shipType, orientation, init_coordinate);

        for (Coordinate coordinate : shipCoordinates) {
            if (coordinate.row() < 0 || coordinate.row() >= grid.getGrid().length || coordinate.col() < 0 || coordinate.col() >= grid.getGrid()[0].length){
                throw new IllegalArgumentException("The ship coordinates must respect the grid dimension");
            }
        }

        Set<Coordinate> coordinatesSet = new HashSet<>(shipCoordinates);

        switch (shipType){
            case CARRIER: return new Carrier(coordinatesSet, shipType);
            case CRUISER: return new Cruiser(coordinatesSet, shipType);
            case BATTLESHIP: return new Battleship(coordinatesSet, shipType);
            case SUBMARINE: return new Submarine(coordinatesSet, shipType);
            case DESTROYER: return new Destroyer(coordinatesSet, shipType);
            default:
                throw new IllegalArgumentException("Ship type not supported: " + shipType);
        }
    }

    /**
     * Computes the coordinates of a ship based on its type, orientation, and initial starting coordinate.
     * The method generates a list of all the coordinates that the ship occupies, starting from the given
     * initial coordinate and extending in the specified orientation.
     *
     * @param shipType the type of the ship, such as CARRIER, BATTLESHIP, etc.; must not be null
     * @param orientation the orientation of the ship, such as HORIZONTAL_RIGHT, VERTICAL_DOWN, etc.; must not be null
     * @param init_coordinate the initial starting coordinate of the ship on the grid; must not be null
     * @return a list of coordinates representing the positions of the ship based on its type and orientation
     */
    private List<Coordinate> computeShipCoordinates(@NonNull ShipType shipType,
                                                    @NonNull Orientation orientation,
                                                    @NonNull Coordinate init_coordinate){
        List<Coordinate> shipCoordinates = new ArrayList<>(shipType.getSize());
        if (Orientation.VERTICAL_UP.equals(orientation)){
            for (int i = 0; i < shipType.getSize(); i++) {
                shipCoordinates.add(new Coordinate(init_coordinate.row() - i, init_coordinate.col()));
            }
        }else if (Orientation.HORIZONTAL_LEFT.equals(orientation)){
            for (int i = 0; i < shipType.getSize(); i++) {
                shipCoordinates.add(new Coordinate(init_coordinate.row(), init_coordinate.col() - i));
            }
        }
        else if (Orientation.HORIZONTAL_RIGHT.equals(orientation)) {
            for (int i = 0; i < shipType.getSize(); i++) {
                shipCoordinates.add(new Coordinate(init_coordinate.row(), init_coordinate.col() + i));
            }
        }else {
            for (int i = 0; i < shipType.getSize(); i++) {
                shipCoordinates.add(new Coordinate(init_coordinate.row() + i, init_coordinate.col()));
            }
        }
        return shipCoordinates;
    }
}
