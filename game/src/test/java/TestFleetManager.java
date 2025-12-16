import battleship.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestFleetManager {

    private FleetManager fleetManager;
    private Grid grid;
    @BeforeEach
    void setUp() {
        grid = new Grid(10,10);
        fleetManager = new FleetManager(grid);
    }

    @Test
    void addShipTest_emptyFleet(){
        Ship ship = Ship.createShip(new Coordinate(0,0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        assertTrue(fleetManager.addShip(ship), "Expected the method to return true when adding a ship to an empty fleet.");
    }

    @Test
    void testAddShipOverlapping() {
        Ship ship1 = Ship.createShip(new Coordinate(3, 3), Orientation.HORIZONTAL_RIGHT, ShipType.CRUISER, grid);
        fleetManager.addShip(ship1);
        Ship ship2 = Ship.createShip(new Coordinate(3, 4), Orientation.HORIZONTAL_RIGHT, ShipType.FRIGATE, grid);
        boolean result = fleetManager.addShip(ship2);

        assertFalse(result, "Expected the method to return false when adding a ship overlapping with an existing ship.");
    }

    @Test
    void testAddShipTooClose() {
        Ship ship1 = Ship.createShip(new Coordinate(4, 4), Orientation.HORIZONTAL_RIGHT, ShipType.CRUISER, grid);
        fleetManager.addShip(ship1);
        Ship ship2 = Ship.createShip(new Coordinate(5, 5), Orientation.VERTICAL_DOWN, ShipType.DESTROYER, grid);
        boolean result = fleetManager.addShip(ship2);

        assertFalse(result, "Expected the method to return false when adding a ship too close to an existing ship.");
    }

    @Test
    void testAddShipValidPlacement() {
        Ship ship1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.CRUISER, grid);
        fleetManager.addShip(ship1);
        Ship ship2 = Ship.createShip(new Coordinate(5, 5), Orientation.VERTICAL_DOWN, ShipType.FRIGATE, grid);
        boolean result = fleetManager.addShip(ship2);

        assertTrue(result, "Expected the method to return true when adding a ship with valid placement.");
    }

    @Test
    void testAddShipOutOfBounds() {
        assertThrows(IllegalArgumentException.class,
                () -> Ship.createShip(new Coordinate(11, 0), Orientation.HORIZONTAL_RIGHT, ShipType.CARRIER, grid),
                "Expected an IllegalArgumentException when placing a ship out of grid bounds.");
    }

    @Test
    void testRemoveShipFromCoordinate_ShipByReferenceExistsAtCoordinate() {
        Ship ship1 = Ship.createShip(new Coordinate(2, 3),  Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        Ship ship2 = Ship.createShip(new Coordinate(5, 5), Orientation.VERTICAL_DOWN, ShipType.FRIGATE, grid);

        fleetManager.addShip(ship1);
        fleetManager.addShip(ship2);

        boolean result = fleetManager.removeShipByCoordinate(new Coordinate(2, 3));

        assertTrue(result, "Expected the method to return true when a ship exists at the given coordinate.");
        assertFalse(fleetManager.removeShipByReference(ship1), "The ship should have been removed from the fleet.");
    }

    @Test
    void testRemoveShipFromCoordinate_NoShipByReferenceExistsAtCoordinate() {
        Ship ship = Ship.createShip(new Coordinate(4, 4), Orientation.HORIZONTAL_RIGHT, ShipType.BATTLESHIP, grid);
        fleetManager.addShip(ship);

        boolean result = fleetManager.removeShipByCoordinate(new Coordinate(7, 7));

        assertFalse(result, "Expected the method to return false when no ship exists at the given coordinate.");
    }

    @Test
    void testRemoveShipByReferenceByCoordinate_ExactMatchForMiddleCoordinate() {
        Ship ship = Ship.createShip(new Coordinate(3, 3),Orientation.HORIZONTAL_RIGHT, ShipType.CRUISER, grid );
        fleetManager.addShip(ship);

        boolean result = fleetManager.removeShipByCoordinate(new Coordinate(3, 4));

        assertTrue(result, "Expected the method to return true when a ship occupies the given coordinate.");
        assertFalse(fleetManager.removeShipByReference(ship), "The ship should have been removed from the fleet.");
    }

    @Test
    void testRemoveShipByReferenceByCoordinate_EmptyFleet() {
        boolean result = fleetManager.removeShipByCoordinate(new Coordinate(0, 0));

        assertFalse(result, "Expected the method to return false when attempting to remove from an empty fleet.");
    }

    @Test
    void testRemoveShipByReferenceByCoordinate_NullCoordinate() {
        assertThrows(NullPointerException.class,
                () -> fleetManager.removeShipByCoordinate(null),
                "Expected a NullPointerException when the given coordinate is null.");
    }

    @Test
    void testRemoveShipByReferenceByCoordinate_AllShipsRemoved() {
        Ship ship1 = Ship.createShip(new Coordinate(1, 1), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        Ship ship2 = Ship.createShip(new Coordinate(3, 3), Orientation.HORIZONTAL_RIGHT, ShipType.CARRIER, grid);
        fleetManager.addShip(ship1);
        fleetManager.addShip(ship2);

        fleetManager.removeShipByCoordinate(new Coordinate(1, 1));
        fleetManager.removeShipByCoordinate(new Coordinate(3, 3));

        assertFalse(fleetManager.removeShipByCoordinate(new Coordinate(1, 1)),
                "Expected the method to remove ships that had matched their coordinates earlier.");
    }

    @Test
    void testGetShipByCoordinate_ShipExistsAtSpecificCoordinate() {
        Ship ship = Ship.createShip(new Coordinate(2, 3), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship);

        Ship retrievedShip = fleetManager.getShipByCoordinate(new Coordinate(2, 3));

        assertNotNull(retrievedShip, "Expected a non-null ship when a ship exists at the given coordinate.");
        assertEquals(ShipType.DESTROYER, retrievedShip.getShipType(), "Expected the retrieved ship to match the one positioned at the coordinate.");
    }

    @Test
    void testGetShipByCoordinate_NoShipAtSpecificCoordinate() {
        Ship ship = Ship.createShip(new Coordinate(2, 3), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship);

        Ship retrievedShip = fleetManager.getShipByCoordinate(new Coordinate(5, 5));

        assertNull(retrievedShip, "Expected a null return value when no ship is found at the given coordinate.");
    }

    @Test
    void testGetShipByCoordinate_MultipleShips_FindsCorrectShip() {
        Ship ship1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.CARRIER, grid);
        Ship ship2 = Ship.createShip(new Coordinate(3, 3), Orientation.VERTICAL_DOWN, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship1);
        fleetManager.addShip(ship2);
        Ship retrievedShip = fleetManager.getShipByCoordinate(new Coordinate(3, 3));

        assertNotNull(retrievedShip, "Expected a non-null ship when a ship exists at the given coordinate.");
        assertEquals(ShipType.DESTROYER, retrievedShip.getShipType(), "Expected the retrieved ship to be the one at the given coordinate.");
    }

    @Test
    void testGetShipByCoordinate_EmptyFleet() {
        Ship retrievedShip = fleetManager.getShipByCoordinate(new Coordinate(0, 0));

        assertNull(retrievedShip, "Expected a null return value when the fleet is empty.");
    }

    @Test
    void testGetShipByCoordinate_NullCoordinate() {
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship);

        assertThrows(NullPointerException.class, () -> fleetManager.getShipByCoordinate(null), "Expected a NullPointerException when the coordinate is null.");
    }

    @Test
    void testGetShipByReference_ShipExistsInFleet() {
        Ship expectedShip = Ship.createShip(new Coordinate(2, 2), Orientation.HORIZONTAL_RIGHT, ShipType.CARRIER, grid);
        fleetManager.addShip(expectedShip);

        Ship retrievedShip = fleetManager.getShipByReference(expectedShip);

        assertNotNull(retrievedShip, "Expected a non-null ship when the specified ship exists in the fleet.");
        assertEquals(expectedShip, retrievedShip, "Expected the retrieved ship to match the specified ship reference.");
    }

    @Test
    void testGetShipByReference_ShipDoesNotExistInFleet() {
        Ship nonExistentShip = Ship.createShip(new Coordinate(4, 4), Orientation.HORIZONTAL_RIGHT,ShipType.DESTROYER, grid);
        Ship ship = Ship.createShip(new Coordinate(2, 2), Orientation.HORIZONTAL_RIGHT, ShipType.CARRIER, grid);
        fleetManager.addShip(ship);

        Ship retrievedShip = fleetManager.getShipByReference(nonExistentShip);

        assertNull(retrievedShip, "Expected a null value when the specified ship does not exist in the fleet.");
    }

    @Test
    void testGetShipByReference_NullInput() {
        assertThrows(NullPointerException.class,
                () -> fleetManager.getShipByReference(null),
                "Expected a NullPointerException when the ship reference is null.");
    }

    @Test
    void testGetShipByReference_EmptyFleet() {
        Ship ship = Ship.createShip(new Coordinate(5, 5), Orientation.VERTICAL_UP,ShipType.FRIGATE, grid);

        Ship retrievedShip = fleetManager.getShipByReference(ship);

        assertNull(retrievedShip, "Expected a null value when attempting to retrieve a ship from an empty fleet.");
    }
}
