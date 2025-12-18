package battleship.model;

import it.units.battleship.Coordinate;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The FleetManager class is responsible for managing a fleet of ships within a specified grid.
 * It handles the addition of ships to the fleet based on their type, orientation, and starting coordinates,
 * ensuring that the ships are placed according to game rules and do not overlap or come too close to each other.
 */
public class FleetManager {

    /**
     * The minimum distance threshold that must be maintained between ships
     * on the game grid in order to ensure valid placement. This constant
     * is used to determine whether the placement of a new ship is too close
     * to an existing one, thereby violating the rules of the game.
     */
    private static final int MIN_DISTANCE_THRESHOLD = 1;

    private final Grid grid;
    private final List<Ship> fleet = new ArrayList<>();

    public FleetManager(@NonNull Grid grid){
        this.grid = grid;
    }

    /**
     * Adds a ship to the fleet if its placement on the grid is valid. A placement is
     * considered valid if the ship does not overlap with or come too close to any
     * other ship already in the fleet.
     *
     * @param ship the ship to be added to the fleet; must be non-null
     * @return true if the ship is successfully added to the fleet, false if the
     *         placement is invalid
     */
    public boolean addShip(@NonNull Ship ship){
        if (!isPlacementValid(ship)){
            return false;
        }
        fleet.add(ship);
        return true;
    }

    /**
     * Determines if the placement of a new ship on the grid is valid by checking
     * against all already existing ships in the fleet. A placement is considered
     * valid if the new ship does not overlap or come too close to any other ship.
     *
     * @param ship the ship whose placement is being validated; must be non-null
     * @return true if the placement is valid and the ship can be placed without
     *         conflict; false otherwise
     */
    private boolean isPlacementValid(@NonNull Ship ship){
        if (fleet.isEmpty()){
            return true;
        }else {
            for(Ship existingShip : fleet) {
                if (!areShipOverlapping(existingShip, ship)){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean areShipOverlapping(@NonNull Ship existingShip, @NonNull Ship ship){
        for(Coordinate coordinate : existingShip.getCoordinates()) {
            for( Coordinate existingCoordinate : ship.getCoordinates()){
                int dx = Math.abs(coordinate.row() - existingCoordinate.row());
                int dy = Math.abs(coordinate.col() - existingCoordinate.col());

                // Invalid if ships overlap or touch (also diagonally).
                // Requiring at least 1 empty cell between ships means max(dx, dy) must be >= 2.
                if (Math.max(dx, dy) <= MIN_DISTANCE_THRESHOLD){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Removes a ship from the fleet. If the specified ship exists in the fleet, it is removed.
     *
     * @param ship the ship to be removed; must be non-null
     * @return true if the ship was successfully removed from the fleet, false if the ship was not found
     */
    public boolean removeShipByReference(@NonNull Ship ship){
        return fleet.remove(ship);
    }

    /**
     * Removes a ship from the fleet if it occupies the specified coordinate.
     *
     * @param coordinate the coordinate to check for ship removal; must be non-null
     * @return true if a ship occupying the specified coordinate was successfully removed, false otherwise
     */
    public boolean removeShipByCoordinate(@NonNull Coordinate coordinate){
        return fleet.removeIf(ship -> ship.getCoordinates().contains(coordinate));
    }

    /**
     * Retrieves a ship from the fleet that matches the provided ship reference.
     *
     * @param ship the ship to be searched in the fleet; must be non-null
     * @return the matching ship from the fleet if found, or {@code null} if no match exists
     */
    public Ship getShipByReference(@NonNull Ship ship){
        return fleet.stream().filter(ship::equals).findFirst().orElse(null);
    }

    /**
     * Retrieves the ship located at the specified coordinate from the fleet.
     *
     * @param coordinate the coordinate to check for a ship; must be non-null
     * @return the ship located at the given coordinate, or null if no ship is found or the coordinate is invalid
     */
    public Ship getShipByCoordinate(@NonNull Coordinate coordinate){
        return fleet.stream().filter(ship -> ship.getCoordinates().contains(coordinate)).findFirst().orElse(null);
    }
}
