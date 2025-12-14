import it.units.battleship.model.*;
import it.units.battleship.model.ShipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestFleetFactory {
    private FleetFactory factory;
    private Grid grid;

    @BeforeEach
    void setUp() {
        Grid grid = new Grid(10, 10);
        factory = new FleetFactory(grid);
    }
    @Test
    void testCreateShip_CreatesCarrierSuccessfully() {

        IShip ship = factory.createShip(
                ShipType.CARRIER, Orientation.HORIZONTAL_RIGHT, new Coordinate(0, 0));

        assertNotNull(ship);
        assertEquals(ShipType.CARRIER, ship.getShipType());
        assertEquals(7, ship.getSize());
    }

    @Test
    void testCreateShip_ValidVerticalUpOrientation() {
        IShip ship = factory.createShip(
                ShipType.CRUISER, Orientation.VERTICAL_UP, new Coordinate(5, 5));

        Set<Coordinate> expectedCoordinates = Set.of(
                new Coordinate(5, 5),
                new Coordinate(4, 5),
                new Coordinate(3, 5),
                new Coordinate(2, 5));

        assertEquals(expectedCoordinates, ((StandardShip) ship).getCoordinates());
    }

    @Test
    void testCreateShip_ThrowsExceptionForOutOfBoundsPlacement() {
        Coordinate initCoordinate = new Coordinate(9, 9);

        assertThrows(IllegalArgumentException.class,
                () -> factory.createShip(ShipType.CRUISER, Orientation.HORIZONTAL_RIGHT, initCoordinate));
    }

    @Test
    void testCreateShip_HorizontalLeftSuccessfully() {
        IShip ship = factory.createShip(
                ShipType.DESTROYER, Orientation.HORIZONTAL_LEFT, new Coordinate(5, 5));

        Set<Coordinate> expectedCoordinates = Set.of(
                new Coordinate(5, 5),
                new Coordinate(5, 4));

        assertEquals(expectedCoordinates, ((StandardShip) ship).getCoordinates());
    }

    @Test
    void testCreateShip_ThrowsExceptionForUnsupportedShipType() {
        ShipType unsupportedType = null;

        assertThrows(NullPointerException.class,
                () -> factory.createShip(unsupportedType, Orientation.HORIZONTAL_RIGHT, new Coordinate(0, 0)));
    }

    @Test
    void testCreateShip_ValidVerticalDownOrientation() {
        IShip ship = factory.createShip(
                ShipType.FRIGATE, Orientation.VERTICAL_DOWN, new Coordinate(2, 2));

        Set<Coordinate> expectedCoordinates = Set.of(
                new Coordinate(2, 2),
                new Coordinate(3, 2),
                new Coordinate(4, 2));

        assertEquals(expectedCoordinates, ((StandardShip) ship).getCoordinates());
    }

    @Test
    void testCreateShip_ThrowsExceptionForInvalidCoordinateInput() {
        Coordinate invalidCoordinate = new Coordinate(-1, -1);

        assertThrows(IllegalArgumentException.class,
                () -> factory.createShip(ShipType.CARRIER, Orientation.VERTICAL_UP, invalidCoordinate));
    }

    @Test
    void testCreateShip_ThrowsExceptionWhenShipExceedsGridBoundary() {
        Coordinate initCoordinate = new Coordinate(8, 8);

        assertThrows(IllegalArgumentException.class,
                () -> factory.createShip(ShipType.BATTLESHIP, Orientation.HORIZONTAL_RIGHT, initCoordinate));
    }

    @Test
    void testCreateShip_CreatesDestroyerSuccessfully() {
        IShip ship = factory.createShip(
                ShipType.DESTROYER, Orientation.HORIZONTAL_RIGHT, new Coordinate(1, 1));

        assertNotNull(ship);
        assertEquals(ShipType.DESTROYER, ship.getShipType());
        assertEquals(2, ship.getSize());

        Set<Coordinate> expectedCoordinates = Set.of(
                new Coordinate(1, 1),
                new Coordinate(1, 2));

        assertEquals(expectedCoordinates, ((StandardShip) ship).getCoordinates());
    }

    @Test
    void testCreateShip_ThrowsExceptionForNullOrientation() {
        Grid grid = new Grid(10, 10);
        FleetFactory factory = new FleetFactory(grid);

        assertThrows(NullPointerException.class,
                () -> factory.createShip(ShipType.CARRIER, null, new Coordinate(0, 0)));
    }

}
