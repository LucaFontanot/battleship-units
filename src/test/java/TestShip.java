import it.units.battleship.model.Coordinate;
import it.units.battleship.model.ShipType;
import it.units.battleship.model.StandardShip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestShip {

    private static class TestShipImpl extends StandardShip {
        TestShipImpl(Set<Coordinate> coordinates, ShipType type) {
            super(coordinates, type);
        }
    }

    private StandardShip testShip;
    private Set<Coordinate> testCoordinates;

    @BeforeEach
    void setUp() {
        testCoordinates = Set.of(new Coordinate(0,0), new Coordinate(0,1));
        ShipType testType = ShipType.DESTROYER;
        testShip = new TestShipImpl(testCoordinates, testType);
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
