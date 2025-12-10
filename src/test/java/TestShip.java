import it.units.battleship.model.Coordinate;
import it.units.battleship.model.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestShip {

    private static class TestShipImpl extends Ship {
        TestShipImpl(Set<Coordinate> coordinates) {
            super(coordinates);
        }
    }

    private Ship testShip;
    private Set<Coordinate> testCoordinates;

    @BeforeEach
    void setUp() {
        testCoordinates = Set.of(new Coordinate(0,0), new Coordinate(1,1));
        testShip = new TestShipImpl(testCoordinates);
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
    void testIsSunk(){
        testShip.addHit(new Coordinate(0,0));
        testShip.addHit(new Coordinate(1,1));
        assertTrue(testShip.isSunk());
    }
}
