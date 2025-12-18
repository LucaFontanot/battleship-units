package battleship.model;

import it.units.battleship.Coordinate;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * The ShipType enum represents different types of ships in the Battleship game.
 * Each ship type has a specified size, name, and its predefined frame in terms of relative coordinates.
 *
 * The available ship types are:
 * - CARRIER: A large ship with a size of 7 units.
 * - BATTLESHIP: A ship with a size of 5 units.
 * - CRUISER: A medium-sized ship with a size of 4 units.
 * - FRIGATE: A small ship with a size of 3 units.
 * - DESTROYER: A minimal ship with a size of 2 units.
 *
 * The ship frame defines the relative layout of the ship, based on a set of coordinates
 * representing the arrangement of its occupied cells.
 *
 * Additionally, the enum provides functionality to calculate the actual coordinates
 * of the ship on the grid, given an initial coordinate and an orientation.
 */
public enum ShipType {
    CARRIER(7, "Carrier", List.of(new Coordinate(0, 0), new Coordinate(0, 1), new Coordinate(0, 2), new Coordinate(0,3), new Coordinate(1,0), new Coordinate(1,1), new Coordinate(1,2))),
    BATTLESHIP(5, "Battleship", List.of(new Coordinate(0,0), new Coordinate(0,1), new Coordinate(0,2), new Coordinate(0,3), new Coordinate(0,4))),
    CRUISER(4, "Cruiser", List.of(new Coordinate(0,0), new Coordinate(0,1), new Coordinate(0,2), new Coordinate(0,3))),
    FRIGATE(3, "Frigate", List.of(new Coordinate(0,0), new Coordinate(0,1), new Coordinate(0,2))),
    DESTROYER(2, "Destroyer", List.of(new Coordinate(0,0), new Coordinate(0,1))),;

    @Getter
    private final int size;
    @Getter
    private final String name;
    @Getter
    private final List<Coordinate> shipFrame;

    ShipType(int size, @NonNull String name, List<Coordinate> shipFrame) {
        if (shipFrame.size() != size) {
            throw new IllegalArgumentException("Ship frame must have the same number of cells as its size");
        }if (name.isBlank()) {
            throw new IllegalArgumentException("Ship name cannot be blank");
        }
        this.size = size;
        this.name = name;
        this.shipFrame = shipFrame;
    }

    /**
     * Calculates the absolute coordinates of a ship on the grid based on its initial coordinate
     * and orientation.
     *
     * @param initCoordinate the initial coordinate of the ship's starting position, must not be null
     * @param orientation the orientation of the ship (e.g., horizontal or vertical), must not be null
     * @return a set of {@link Coordinate} objects representing the ship's absolute positions on the grid
     */
    public Set<Coordinate> getShipCoordinates(@NonNull Coordinate initCoordinate,@NonNull Orientation orientation){
        return computeShipCoordinates(orientation.getAngle(), initCoordinate);
    }

    /**
     * Computes the absolute coordinates of a ship using its initial coordinate and rotation angle on the grid.
     *
     * @param theta the rotation angle in radians to apply to the ship's frame
     * @param initCoordinate the initial coordinate of the ship's starting position
     * @return a set of {@code Coordinate} objects representing the computed absolute positions of the ship
     */
    private Set<Coordinate> computeShipCoordinates(Double theta, Coordinate initCoordinate){
        Double[][] rotation_matrix = {{Math.cos(theta), -Math.sin(theta)},
                                        {Math.sin(theta), Math.cos(theta)}};

        return getShipFrame()
                .stream()
                .map(coordinate -> {
                    int x = coordinate.row();
                    int y = coordinate.col();
                    return new Coordinate((int) ( initCoordinate.row()+(rotation_matrix[0][0] * x + rotation_matrix[0][1] * y)),
                                          (int) ( initCoordinate.col()+(rotation_matrix[1][0] * x + rotation_matrix[1][1] * y)));
                }).collect(toSet());
    }
}
