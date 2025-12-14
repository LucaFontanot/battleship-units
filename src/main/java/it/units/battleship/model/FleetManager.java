package it.units.battleship.model;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The FleetManager class is responsible for managing a fleet of ships within a specified grid.
 * It handles the addition of ships to the fleet based on their type, orientation, and starting coordinates,
 * ensuring that the ships are placed according to game rules and do not overlap or come too close to each other.
 */
public class FleetManager {
    private final Grid grid;
    private final FleetFactory factory;
    private final List<IShip> fleet = new ArrayList<>();

    public FleetManager(@NonNull Grid grid,@NonNull FleetFactory factory){
        if (!factory.getGrid().equals(grid)){
            throw new IllegalArgumentException("Factory's grid must be equal to the grid provided as argument");
        }
        this.grid = grid;
        this.factory = factory;
    }

    /**
     * Adds a ship to the fleet based on the specified type, orientation, and starting coordinate.
     * The method validates the placement of the ship to ensure it does not overlap
     * with existing ships or violate the placement rules before adding it to the fleet.
     *
     * @param type the type of the ship to be added (e.g., CARRIER, BATTLESHIP, etc.)
     * @param orientation the orientation of the ship (e.g., HORIZONTAL_RIGHT, VERTICAL_UP, etc.)
     * @param init_coordinate the starting coordinate for the ship's placement on the grid
     * @return true if the ship was successfully added to the fleet, false if the placement was invalid
     */
    public boolean addShip(@NonNull ShipType type,@NonNull Orientation orientation,@NonNull Coordinate init_coordinate){
        IShip ship = factory.createShip(type, orientation, init_coordinate);
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
    private boolean isPlacementValid(@NonNull IShip ship){
        if (fleet.isEmpty()){
            return true;
        }else {
            for(IShip existingShip : fleet) {
                for(Coordinate coordinate : existingShip.getCoordinates()) {
                    for( Coordinate existingCoordinate : ship.getCoordinates()){
                        int deltaX = Math.abs(coordinate.row() - existingCoordinate.row());
                        int deltaY = Math.abs(coordinate.col() - existingCoordinate.col());
                        //Check if the ships overlap or are too close to each other.
                        if (deltaX <= 1 || deltaY <= 1){
                            return false;
                        }
                    }
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
    public boolean removeShip(@NonNull IShip ship){
        return fleet.remove(ship);
    }

    /**
     * Removes a ship from the fleet if it occupies the specified coordinate.
     *
     * @param coordinate the coordinate to check for ship removal; must be non-null
     * @return true if a ship occupying the specified coordinate was successfully removed, false otherwise
     */
    public boolean removeShipFromCoordinate(@NonNull Coordinate coordinate){
        return fleet.removeIf(ship -> ship.getCoordinates().contains(coordinate));
    }
}
