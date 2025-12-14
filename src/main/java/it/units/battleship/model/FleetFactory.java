package it.units.battleship.model;

import lombok.NonNull;

import java.lang.Math;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

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
    public IShip createShip(@NonNull ShipType shipType,
                           @NonNull Orientation orientation,
                           @NonNull Coordinate init_coordinate) throws IllegalArgumentException {
        List<Coordinate> shipCoordinates = computeShipCoordinates(shipType, orientation, init_coordinate);

        for (Coordinate coordinate : shipCoordinates) {
            if (coordinate.row() < 0 || coordinate.row() >= grid.getRow() || coordinate.col() < 0 || coordinate.col() >= grid.getCol()){
                throw new IllegalArgumentException("The ship coordinates must respect the grid dimension");
            }
        }

        Set<Coordinate> coordinatesSet = new HashSet<>(shipCoordinates);

        switch (shipType){
            case CARRIER:
            case CRUISER:
            case BATTLESHIP:
            case FRIGATE:
            case DESTROYER:
                return new StandardShip(coordinatesSet, shipType);
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
        return computeCoordinateRotated(shipType, orientation.getAngle(), init_coordinate);
    }

    /**
     * Computes the rotated coordinates of a ship based on its type, a given rotation angle,
     * and an initial reference coordinate. The method applies a rotation transformation
     * using a rotation matrix to calculate the new positions of all coordinates.
     *
     * @param shipType the type of the ship (e.g., CARRIER, BATTLESHIP, CRUISER); must not be null
     * @param theta the rotation angle in radians to apply to the ship's coordinates
     * @param init_coordinate the initial reference coordinate for rotation; must not be null
     * @return a list of rotated coordinates representing the positions of the ship after the rotation
     */
    private List<Coordinate> computeCoordinateRotated(ShipType shipType, Double theta, Coordinate init_coordinate){
          Double[][] rotation_matrix = {{Math.cos(theta), -Math.sin(theta)},
                                        {Math.sin(theta), Math.cos(theta)}};

           return shipType.getShipFrame()
                   .stream()
                   .map(coordinate -> {
                       int x = coordinate.row();
                       int y = coordinate.col();
                       return new Coordinate((int) ( init_coordinate.row()+(rotation_matrix[0][0] * x + rotation_matrix[0][1] * y)),
                                            (int) ( init_coordinate.col()+(rotation_matrix[1][0] * x + rotation_matrix[1][1] * y)));
                   }).collect(toList());
    }
}
