import it.units.battleship.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestFleetManager {

    private FleetManager fleetManager;
    @Test
    void testFleetManager(){
        Grid grid = new Grid(10,10);
        FleetFactory f = new FleetFactory(grid);

        Grid grid_wrong = new Grid(5,5);
        assertThrows(IllegalArgumentException.class, () -> new FleetManager(grid_wrong, f), "Expected an IllegalArgumentException when the factory's grid does not match the FleetManager's grid.");
    }

    @BeforeEach
    void setUp() {
        Grid grid = new Grid(10,10);
        fleetManager = new FleetManager(grid, new FleetFactory(grid));
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
}
