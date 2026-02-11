package battleship.model;

import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestShip {

    private Ship testShip;
    private Set<Coordinate> testCoordinates;

    @Test
    void testCreateShip_ValidPlacement() {
        Grid grid = new Grid(10, 10);
        Coordinate initCoordinate = new Coordinate(3, 3);
        ShipType type = ShipType.BATTLESHIP;
        Orientation orientation = Orientation.HORIZONTAL_RIGHT;

        Ship ship = Ship.createShip(initCoordinate, orientation, type, grid);

        assertNotNull(ship, "Expected a non-null Ship instance to be created.");
        assertEquals(type, ship.getShipType(), "Expected the ship type to match the input type.");
        assertEquals(type.getSize(), ship.getCoordinates().size(), "Expected the ship size to match the type's size.");
    }

    /**
     * Tests the creation of a ship that goes out of the grid bounds, expecting an exception.
     */
    @Test
    void testCreateShip_OutOfBounds() {
        Grid grid = new Grid(5, 5);
        Coordinate initCoordinate = new Coordinate(4, 4);  // Starting near the edge
        ShipType type = ShipType.BATTLESHIP;               // Large ship
        Orientation orientation = Orientation.HORIZONTAL_RIGHT;

        assertThrows(IllegalArgumentException.class, () ->
                        Ship.createShip(initCoordinate, orientation, type, grid),
                "Expected IllegalArgumentException for a ship out of grid bounds."
        );
    }

    /**
     * Tests the creation of a ship where the coordinates do not match the required ship size.
     */
    @Test
    void testCreateShip_InvalidSize() {
        Grid grid = new Grid(10, 10);
        Coordinate initCoordinate = new Coordinate(0, 0);
        ShipType type = ShipType.DESTROYER; // Small ship with size = 2

        assertThrows(IllegalArgumentException.class, () ->
                        Ship.createShip(initCoordinate, Orientation.HORIZONTAL_LEFT, type, grid),
                "Expected IllegalArgumentException due to invalid ship size."
        );
    }

    /**
     * Tests the creation of a carrier ship with valid coordinates.
     */
    @Test
    void testCreateShip_CarrierValidCoordinates() {
        Grid grid = new Grid(10, 10);
        Coordinate initCoordinate = new Coordinate(2, 2);
        ShipType type = ShipType.CARRIER;
        Orientation orientation = Orientation.HORIZONTAL_RIGHT;

        Ship ship = Ship.createShip(initCoordinate, orientation, type, grid);

        assertNotNull(ship, "Expected a valid ship to be created.");
        assertEquals(type.getSize(), ship.getCoordinates().size(),
                "Expected the ship to have coordinates matching its size.");
    }

    /**
     * Tests the creation of a ship with invalid initial coordinates (negative row).
     */
    @Test
    void testCreateShip_InvalidInitialCoordinates() {
        Grid grid = new Grid(10, 10);
        Coordinate initCoordinate = new Coordinate(-1, 3); // Negative row
        ShipType type = ShipType.FRIGATE;                // Small ship
        Orientation orientation = Orientation.VERTICAL_DOWN;

        assertThrows(IllegalArgumentException.class, () ->
                        Ship.createShip(initCoordinate, orientation, type, grid),
                "Expected IllegalArgumentException for invalid initial coordinates."
        );
    }

    /**
     * Tests ship creation with valid grid and orientation.
     */
    @Test
    void testCreateShipWithOrientation() {
        Grid grid = new Grid(10, 10);
        Coordinate initCoordinate = new Coordinate(5, 5);
        ShipType type = ShipType.DESTROYER;
        Orientation orientation = Orientation.VERTICAL_DOWN;

        Ship ship = Ship.createShip(initCoordinate, orientation, type, grid);

        assertNotNull(ship, "Expected a valid ship to be created.");
        assertEquals(type, ship.getShipType(), "Expected ship type to match the input.");
    }

    @BeforeEach
    void setTestShip(){
        Grid grid = new Grid(10, 10);
        Coordinate initCoordinate = new Coordinate(0, 0);
        ShipType type = ShipType.DESTROYER;
        Orientation orientation = Orientation.HORIZONTAL_RIGHT;

        testShip = Ship.createShip(initCoordinate, orientation, type, grid);
    }

    @Test
    void testAddHit_OutsideShip(){
        assertFalse(testShip.addHit(new Coordinate(2,2)));
    }
    
    @Test
    void testAddHit_OnShip(){
        testShip.addHit(new Coordinate(0,0));
        Set<Coordinate> expected = new HashSet<>();
        expected.add(new Coordinate(0, 0));
        assertTrue(testShip.getHitCoordinates().equals(expected));
    }

    @Test
    void testAddHit_OnShipTwice(){
        testShip.addHit(new Coordinate(0,0));
        assertFalse(testShip.addHit(new Coordinate(0,0)));
    }

    @Test
    void testIsSunk_AllCoordinates(){
        testShip.addHit(new Coordinate(0,0));
        testShip.addHit(new Coordinate(0,1));
        assertTrue(testShip.isSunk());
    }

    @Test
    void testIsSunk_PartialHits() {
        testShip.addHit(new Coordinate(0, 0));
        assertFalse(testShip.isSunk(), "Expected ship to not be sunk when not all coordinates are hit.");
    }

    @Test
    void testIsSunk_NoHits() {
        // Assert ship is not sunk with no hits
        assertFalse(testShip.isSunk(), "Expected ship to not be sunk when no coordinates are hit.");
    }
}
