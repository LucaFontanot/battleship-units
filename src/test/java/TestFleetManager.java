import it.units.battleship.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestFleetManager {

    private FleetManager fleetManager;
    private FleetFactory factory;
    @Test
    void testFleetManager(){
        Grid grid = new Grid(10,10);
        factory = new FleetFactory(grid);

        Grid grid_wrong = new Grid(5,5);
        assertThrows(IllegalArgumentException.class, () -> new FleetManager(grid_wrong, factory), "Expected an IllegalArgumentException when the factory's grid does not match the FleetManager's grid.");
    }

    @BeforeEach
    void setUp() {
        Grid grid = new Grid(10,10);
        factory = new FleetFactory(grid);
        fleetManager = new FleetManager(grid, factory);
    }

    @Test
    void addShipTest_emptyFleet(){
        assertTrue(fleetManager.addShip(ShipType.DESTROYER, Orientation.HORIZONTAL_RIGHT, new Coordinate(0,0)), "Expected the method to return true when adding a ship to an empty fleet.");
    }

    @Test
    void testAddShipOverlapping() {
        fleetManager.addShip(ShipType.CRUISER, Orientation.HORIZONTAL_RIGHT, new Coordinate(3, 3));
        boolean result = fleetManager.addShip(ShipType.FRIGATE, Orientation.HORIZONTAL_RIGHT, new Coordinate(3, 4));

        assertFalse(result, "Expected the method to return false when adding a ship overlapping with an existing ship.");
    }

    @Test
    void testAddShipTooClose() {
        fleetManager.addShip(ShipType.CRUISER, Orientation.HORIZONTAL_RIGHT, new Coordinate(4, 4));
        boolean result = fleetManager.addShip(ShipType.DESTROYER, Orientation.VERTICAL_DOWN, new Coordinate(5, 5));

        assertFalse(result, "Expected the method to return false when adding a ship too close to an existing ship.");
    }

    @Test
    void testAddShipValidPlacement() {
        fleetManager.addShip(ShipType.CRUISER, Orientation.HORIZONTAL_RIGHT, new Coordinate(0, 0));
        boolean result = fleetManager.addShip(ShipType.FRIGATE, Orientation.VERTICAL_DOWN, new Coordinate(5, 5));

        assertTrue(result, "Expected the method to return true when adding a ship with valid placement.");
    }

    @Test
    void testAddShipOutOfBounds() {
        assertThrows(IllegalArgumentException.class,
                () -> fleetManager.addShip(ShipType.CARRIER, Orientation.HORIZONTAL_RIGHT, new Coordinate(11, 0)),
                "Expected an IllegalArgumentException when placing a ship out of grid bounds.");
    }

    @Test
    void testFleetManagerInitializationWithMismatchedFactoryGrid() {
        Grid grid = new Grid(10, 10);
        FleetFactory factory = new FleetFactory(new Grid(5, 5));

        assertThrows(IllegalArgumentException.class,
                () -> new FleetManager(grid, factory),
                "Expected an IllegalArgumentException when the factory's grid does not match the FleetManager's grid.");
    }

    @Test
    void testRemoveShipFromCoordinate_ShipExistsAtCoordinate() {
        IShip ship1 = factory.createShip(ShipType.DESTROYER, Orientation.HORIZONTAL_RIGHT, new Coordinate(2, 3));
        IShip ship2 = factory.createShip(ShipType.FRIGATE, Orientation.VERTICAL_DOWN, new Coordinate(5, 5));

        fleetManager.addShip(ShipType.DESTROYER, Orientation.HORIZONTAL_RIGHT, new Coordinate(2, 3));
        fleetManager.addShip(ShipType.FRIGATE, Orientation.VERTICAL_DOWN, new Coordinate(5, 5));

        boolean result = fleetManager.removeShipFromCoordinate(new Coordinate(2, 3));

        assertTrue(result, "Expected the method to return true when a ship exists at the given coordinate.");
        assertFalse(fleetManager.removeShip(ship1), "The ship should have been removed from the fleet.");
    }

    @Test
    void testRemoveShipFromCoordinate_NoShipExistsAtCoordinate() {
        fleetManager.addShip(ShipType.BATTLESHIP, Orientation.HORIZONTAL_RIGHT, new Coordinate(4, 4));

        boolean result = fleetManager.removeShipFromCoordinate(new Coordinate(7, 7));

        assertFalse(result, "Expected the method to return false when no ship exists at the given coordinate.");
    }

    @Test
    void testRemoveShipFromCoordinate_ExactMatchForMiddleCoordinate() {
        IShip ship = factory.createShip(ShipType.CRUISER, Orientation.HORIZONTAL_RIGHT, new Coordinate(3, 3));
        fleetManager.addShip(ShipType.CRUISER, Orientation.HORIZONTAL_RIGHT, new Coordinate(3, 3));

        boolean result = fleetManager.removeShipFromCoordinate(new Coordinate(3, 4));

        assertTrue(result, "Expected the method to return true when a ship occupies the given coordinate.");
        assertFalse(fleetManager.removeShip(ship), "The ship should have been removed from the fleet.");
    }

    @Test
    void testRemoveShipFromCoordinate_EmptyFleet() {
        boolean result = fleetManager.removeShipFromCoordinate(new Coordinate(0, 0));

        assertFalse(result, "Expected the method to return false when attempting to remove from an empty fleet.");
    }

    @Test
    void testRemoveShipFromCoordinate_NullCoordinate() {
        assertThrows(NullPointerException.class,
                () -> fleetManager.removeShipFromCoordinate(null),
                "Expected a NullPointerException when the given coordinate is null.");
    }

    @Test
    void testRemoveShipFromCoordinate_AllShipsRemoved() {
        fleetManager.addShip(ShipType.DESTROYER, Orientation.HORIZONTAL_RIGHT, new Coordinate(1, 1));
        fleetManager.addShip(ShipType.CARRIER, Orientation.HORIZONTAL_RIGHT, new Coordinate(3, 3));

        fleetManager.removeShipFromCoordinate(new Coordinate(1, 1));
        fleetManager.removeShipFromCoordinate(new Coordinate(3, 3));

        assertFalse(fleetManager.removeShipFromCoordinate(new Coordinate(1, 1)),
                "Expected the method to remove ships that had matched their coordinates earlier.");
    }
}
