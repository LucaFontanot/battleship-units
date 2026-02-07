package battleship.model.game;

import it.units.battleship.CellState;
import it.units.battleship.Coordinate;
import it.units.battleship.ShipType;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Getter
    private final Grid grid;
    private final List<Ship> fleet = new ArrayList<>();
    @Getter
    private final Map<ShipType, Integer> requiredFleetConfiguration;

    public FleetManager(@NonNull Grid grid, @NonNull Map<ShipType, Integer> requiredFleetConfiguration){
        this.grid = grid;
        this.requiredFleetConfiguration = requiredFleetConfiguration;
    }

    /**
     * Determines whether the fleet is complete by comparing the current fleet configuration
     * against the required fleet configuration. A fleet is considered complete if it contains
     * the exact number of ships of each type specified in the required fleet configuration.
     *
     * @return true if the current fleet matches the required fleet configuration exactly; false otherwise
     */
    public boolean isFleetComplete(){
        Map<ShipType, Long> currentFleetCounts = fleet.stream().collect(Collectors.groupingBy(Ship::getShipType, Collectors.counting()));

        for (Map.Entry<ShipType, Integer> entry : requiredFleetConfiguration.entrySet()){
            ShipType shipType = entry.getKey();
            int count = entry.getValue();
            Long currentCount = currentFleetCounts.getOrDefault(shipType, 0L);
            if (currentCount != count){
                return false;
            }
        }
        return fleet.size() == requiredFleetConfiguration.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Retrieves an unmodifiable view of the current fleet of ships.
     *
     * @return a containing the ships in the fleet. The returned list is unmodifiable.
     */
    public List<Ship> getFleet() {
        return Collections.unmodifiableList(fleet);
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
        if (!canPlaceShip(ship)){
            return false;
        }
        fleet.add(ship);
        return true;
    }

    /**
     * Calculates the number of remaining ships of a specific type that can be added to the fleet.
     *
     * @param shipType the type of ship to check for remaining count; must be non-null
     * @return the number of remaining ships
     */
    public int getRemaining(@NonNull ShipType shipType) {
        Integer required = requiredFleetConfiguration.get(shipType);
        if (required == null) {
            return 0;
        }
        int placed = countShipsOfType(shipType);
        return Math.max(0, required - placed);
    }

    /**
     * Retrieves the required count of ships for a specific type as defined in the fleet configuration.
     *
     * @param shipType the type of ship to check for required count; must be non-null
     * @return the required count of ships for the specified type
     */
    public int getRequiredCount(@NonNull ShipType shipType) {
        return requiredFleetConfiguration.getOrDefault(shipType, 0);
    }


    /**
     * Determines whether a ship of the given type can be added to the fleet based on the
     * allowed configuration.
     *
     * @param ship the ship to be evaluated; must be non-null
     * @return true if the ship can be added to the fleet based on the configuration, false otherwise
     */
    private boolean canAddShipType(@NonNull Ship ship){
        Integer maxAllowed = requiredFleetConfiguration.get(ship.getShipType());
        if (maxAllowed == null){
            return false;
        }
        return countShipsOfType(ship.getShipType()) < maxAllowed;
    }

    /**
     * Counts the number of ships of the specified type present in the fleet.
     *
     * @param shipType the type of ships to be counted; must be non-null
     * @return the count of ships of the specified type in the fleet
     */
    private int countShipsOfType(@NonNull ShipType shipType){
        return (int) fleet.stream().filter(ship -> ship.getShipType() == shipType).count();
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

    /**
     * Handles an incoming shot targeting the specified coordinate. If the coordinate
     * is occupied by a ship, it records a hit on the ship and updates the grid state
     * to indicate a hit. If no ship is found at the coordinate, the grid state is
     * updated to indicate a miss.
     *
     * @param coordinate the coordinate of the incoming shot; must be non-null
     * @return true if the shot hit or false if is a miss
     */
    public boolean handleIncomingShot(@NonNull Coordinate coordinate){
        Ship ship = getShipByCoordinate(coordinate);
        if (ship != null){
            boolean isNewHit = ship.addHit(coordinate);
            if (isNewHit){
                grid.changeState(coordinate, CellState.HIT);
                if (ship.isSunk()){
                    ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.SUNK));
                }
            }
        }else {
            grid.changeState(coordinate, CellState.MISS);
            return false;
        }
        return true;
    }

    public boolean canPlaceShip(@NonNull Ship ship) {
        return canAddShipType(ship) && isPlacementValid(ship);
    }

    /**
     * Checks whether the game is over by determining if all ships in the fleet have been sunk.
     *
     * @return true if all ships in the fleet are sunk, indicating the game is over; false otherwise
     */
    public boolean isGameOver(){
        return fleet.stream().allMatch(Ship::isSunk);
    }

    /**
     * Calculates the number of ships of each type currently placed in the fleet.
     *
     * @return a map associating each ShipType with the number of ships of that type
     *         already present in the fleet.
     */
    public Map<ShipType, Integer> getPlacedCounts(){
        return fleet.stream()
                .collect(Collectors.groupingBy(
                        Ship::getShipType,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }
}
