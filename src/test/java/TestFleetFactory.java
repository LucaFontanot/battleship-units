import it.units.battleship.model.*;
import it.units.battleship.model.shipType.Carrier;
import it.units.battleship.model.shipType.ShipType;
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
    void testCreateShip_initCoordinateOutOfRange(){
        Coordinate coordinate = new Coordinate(5,11);
        ShipType shipType = ShipType.CARRIER;
        Orientation orientation = Orientation.HORIZONTAL_LEFT;
        assertThrowsExactly(IllegalArgumentException.class, () -> factory.createShip(shipType, orientation, coordinate));
    }

    @Test
    void testCreateShip_invalidShipCoordinates(){
        Coordinate coordinate = new Coordinate(0,0);
        ShipType shipType = ShipType.CARRIER;
        Orientation orientation = Orientation.HORIZONTAL_LEFT;
        assertThrowsExactly(IllegalArgumentException.class, () -> factory.createShip(shipType, orientation, coordinate));
    }

    @Test
    void testCreateShip_HorizontalRight_InBounds() {
        Ship ship = factory.createShip(ShipType.DESTROYER, Orientation.HORIZONTAL_RIGHT, new Coordinate(0, 0));
        Set<Coordinate> expected = Set.of(new Coordinate(0, 0), new Coordinate(0, 1));
        assertEquals(expected, ship.getCoordinates());
        assertEquals(ShipType.DESTROYER.getName(), ship.getShipType());
    }

    @Test
    void testCreateShip_HorizontalLeft_InBounds() {
        Ship ship = factory.createShip(ShipType.DESTROYER, Orientation.HORIZONTAL_LEFT, new Coordinate(5, 5));
        Set<Coordinate> expected = Set.of(new Coordinate(5, 5), new Coordinate(5, 4));
        assertEquals(expected, ship.getCoordinates());
    }

    @Test
    void testCreateShip_VerticalDown_InBounds() {
        Ship ship = factory.createShip(ShipType.DESTROYER, Orientation.VERTICAL_DOWN, new Coordinate(0, 0));
        Set<Coordinate> expected = Set.of(new Coordinate(0, 0), new Coordinate(1, 0));
        assertEquals(expected, ship.getCoordinates());
    }

    @Test
    void testCreateShip_VerticalUp_InBounds() {
        Ship ship = factory.createShip(ShipType.DESTROYER, Orientation.VERTICAL_UP, new Coordinate(5, 5));
        Set<Coordinate> expected = Set.of(new Coordinate(5, 5), new Coordinate(4, 5));
        assertEquals(expected, ship.getCoordinates());
    }

    // Limit testing (Out-of-Bounds)

    @Test
    void testCreateShip_HorizontalRight_OutOfBounds() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createShip(ShipType.DESTROYER, Orientation.HORIZONTAL_RIGHT, new Coordinate(0, 9));
        });
    }

    @Test
    void testCreateShip_HorizontalLeft_OutOfBounds() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createShip(ShipType.DESTROYER, Orientation.HORIZONTAL_LEFT, new Coordinate(0, 0));
        });
    }

    @Test
    void testCreateShip_VerticalDown_OutOfBounds() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createShip(ShipType.DESTROYER, Orientation.VERTICAL_DOWN, new Coordinate(9, 0));
        });
    }

    @Test
    void testCreateShip_VerticalUp_OutOfBounds() {
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createShip(ShipType.DESTROYER, Orientation.VERTICAL_UP, new Coordinate(0, 0));
        });
    }

    @Test
    void testCreateShip_CreatesCorrectShipType() {
        Ship carrier = factory.createShip(ShipType.CARRIER, Orientation.HORIZONTAL_RIGHT, new Coordinate(0, 0));
        assertTrue(carrier instanceof Carrier, "The factory did not create a Carrier ship.");
    }

}
