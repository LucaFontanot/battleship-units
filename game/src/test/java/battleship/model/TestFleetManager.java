package battleship.model;

import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestFleetManager {

    private FleetManager fleetManager;
    private Grid grid;

    @Test
    void handleIncomingShot_sunkShipGridSerialization(){
        grid = new Grid(4,4);
        Map<ShipType, Integer> requiredFleetConfiguration = Map.of(ShipType.DESTROYER, 2, ShipType.CARRIER, 1);
        fleetManager = new FleetManager(grid, requiredFleetConfiguration);
        Ship ship1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship1);

        ship1.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));

        Ship ship2 = Ship.createShip(new Coordinate(0, 3), Orientation.VERTICAL_DOWN, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship2);

        ship2.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));

        assertEquals("KK0K000K00000000",grid.gridSerialization());
    }

    @BeforeEach
    void setUp() {
        grid = new Grid(10,10);
        Map<ShipType, Integer> requiredFleetConfiguration = Map.of(ShipType.DESTROYER, 2, ShipType.CARRIER, 1);
        fleetManager = new FleetManager(grid, requiredFleetConfiguration);
    }

    @Test
    void addShipTest_emptyFleet(){
        Ship ship = Ship.createShip(new Coordinate(0,0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        assertTrue(fleetManager.addShip(ship), "Expected the method to return true when adding a ship to an empty fleet.");
    }

    @Test
    void testAddShipOverlapping() {
        Ship ship1 = Ship.createShip(new Coordinate(3, 3), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship1);
        Ship ship2 = Ship.createShip(new Coordinate(3, 4), Orientation.HORIZONTAL_RIGHT, ShipType.CARRIER, grid);
        boolean result = fleetManager.addShip(ship2);

        assertFalse(result, "Expected the method to return false when adding a ship overlapping with an existing ship.");
    }

    @Test
    void testAddShipTooClose() {
        Ship ship1 = Ship.createShip(new Coordinate(4, 4), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship1);
        Ship ship2 = Ship.createShip(new Coordinate(5, 5), Orientation.VERTICAL_DOWN, ShipType.DESTROYER, grid);
        boolean result = fleetManager.addShip(ship2);

        assertFalse(result, "Expected the method to return false when adding a ship too close to an existing ship.");
    }

    @Test
    void testAddShipValidPlacement() {
        Ship ship1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship1);
        Ship ship2 = Ship.createShip(new Coordinate(5, 5), Orientation.VERTICAL_DOWN, ShipType.DESTROYER, grid);
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
        Ship ship2 = Ship.createShip(new Coordinate(5, 5), Orientation.VERTICAL_DOWN, ShipType.CARRIER, grid);

        fleetManager.addShip(ship1);
        fleetManager.addShip(ship2);

        boolean result = fleetManager.removeShipByCoordinate(new Coordinate(2, 3));

        assertTrue(result, "Expected the method to return true when a ship exists at the given coordinate.");
        assertFalse(fleetManager.removeShipByReference(ship1), "The ship should have been removed from the fleet.");
    }

    @Test
    void testRemoveShipFromCoordinate_NoShipByReferenceExistsAtCoordinate() {
        Ship ship = Ship.createShip(new Coordinate(4, 4), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship);

        boolean result = fleetManager.removeShipByCoordinate(new Coordinate(7, 7));

        assertFalse(result, "Expected the method to return false when no ship exists at the given coordinate.");
    }

    @Test
    void testRemoveShipByReferenceByCoordinate_ExactMatchForMiddleCoordinate() {
        Ship ship = Ship.createShip(new Coordinate(3, 3),Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid );
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
        Ship ship = Ship.createShip(new Coordinate(5, 5), Orientation.VERTICAL_UP,ShipType.CARRIER, grid);

        Ship retrievedShip = fleetManager.getShipByReference(ship);

        assertNull(retrievedShip, "Expected a null value when attempting to retrieve a ship from an empty fleet.");
    }

    @Test
    void handleIncomingShot_successfulHit() {
        Ship ship = Ship.createShip(new Coordinate(2, 2), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);

        fleetManager.addShip(ship);

        Coordinate shotCoordinate = new Coordinate(2, 2);
        fleetManager.handleIncomingShot(shotCoordinate);

        assertEquals(CellState.HIT, grid.getState(shotCoordinate), "Expected the grid cell to be updated to HIT.");
        assertTrue(ship.getHitCoordinates().contains(shotCoordinate), "Expected the shot coordinate to be registered as a hit on the ship.");
    }

    @Test
    void handleIncomingShot_miss() {
        Ship ship = Ship.createShip(new Coordinate(2, 2), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);

        fleetManager.addShip(ship);

        Coordinate shotCoordinate = new Coordinate(3, 3);
        fleetManager.handleIncomingShot(shotCoordinate);

        assertEquals(CellState.MISS, grid.getState(shotCoordinate), "Expected the grid cell to be updated to MISS.");
        assertFalse(ship.getHitCoordinates().contains(shotCoordinate), "Expected the shot coordinate not to be registered as a hit on any ship.");
    }

    @Test
    void handleIncomingShot_hitSameCellAgain() {
        Ship ship = Ship.createShip(new Coordinate(2, 2), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);

        fleetManager.addShip(ship);

        Coordinate shotCoordinate = new Coordinate(2, 2);
        fleetManager.handleIncomingShot(shotCoordinate);
        fleetManager.handleIncomingShot(shotCoordinate);

        assertEquals(CellState.HIT, grid.getState(shotCoordinate), "Expected the grid cell state to remain HIT.");
        assertEquals(1, ship.getHitCoordinates().size(), "Expected the hit coordinates set to contain only one instance of the hit.");
    }

    @Test
    void handleIncomingShot_outOfBounds() {
        Coordinate outOfBoundsCoordinate = new Coordinate(10, 10);

        assertThrows(IndexOutOfBoundsException.class,
                () -> fleetManager.handleIncomingShot(outOfBoundsCoordinate),
                "Expected an IndexOutOfBoundsException when firing at an out-of-bounds coordinate."
        );
    }

    @Test
    void handleIncomingShot_nullCoordinate() {
        assertThrows(NullPointerException.class,
                () -> fleetManager.handleIncomingShot(null),
                "Expected a NullPointerException when firing at a null coordinate."
        );
    }

    @Test
    void handleIncomingShot_onlyMissOnEmptyGrid() {
        Coordinate shotCoordinate = new Coordinate(1, 1);
        fleetManager.handleIncomingShot(shotCoordinate);

        assertEquals(CellState.MISS, grid.getState(shotCoordinate), "Expected the grid cell to be updated to MISS on an empty grid.");
    }

    @Test
    void isGameOver_AllShipsSunk() {
        Ship ship1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship1);

        ship1.getCoordinates().forEach(ship1::addHit);

        assertTrue(fleetManager.isGameOver(), "Expected the game to be over when all ships are sunk.");
    }

    @Test
    void isGameOver_SomeShipsSunk() {
        Ship ship1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        Ship ship2 = Ship.createShip(new Coordinate(2, 2), Orientation.HORIZONTAL_RIGHT, ShipType.CARRIER, grid);
        fleetManager.addShip(ship1);
        fleetManager.addShip(ship2);

        ship1.getCoordinates().forEach(ship1::addHit);

        assertFalse(fleetManager.isGameOver(), "Expected the game not to be over when not all ships are sunk.");
    }

    @Test
    void isGameOver_NoShipsInFleet() {
        assertTrue(fleetManager.isGameOver(), "Expected the game to be over when there are no ships in the fleet.");
    }

    @Test
    void isGameOver_EmptyFleetAfterRemoval() {
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship);
        fleetManager.removeShipByReference(ship);

        assertTrue(fleetManager.isGameOver(), "Expected the game to be over when all ships are removed from the fleet.");
    }

    @Test
    void isGameOver_AllShipsInitiallyUnsunk() {
        Ship ship1 = Ship.createShip(new Coordinate(1, 1), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        Ship ship2 = Ship.createShip(new Coordinate(3, 3), Orientation.HORIZONTAL_RIGHT, ShipType.CARRIER, grid);
        fleetManager.addShip(ship1);
        fleetManager.addShip(ship2);

        assertFalse(fleetManager.isGameOver(), "Expected the game not to be over when no ships are sunk.");
    }

    @Test
    void isGameOver_AllShipsPartiallyDamaged() {
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship);
        LinkedHashSet<Coordinate> coordinates = ship.getCoordinates();

        coordinates.stream().findFirst().ifPresent(ship::addHit);

        assertFalse(fleetManager.isGameOver(), "Expected the game not to be over when ships are only partially damaged.");
    }

    @Test
    void isFleetComplete_EmptyFleet_ShouldReturnFalse() {
        boolean result = fleetManager.isFleetComplete();

        assertFalse(result, "Expected fleet to be incomplete when no ships are added.");
    }

    @Test
    void isFleetComplete_ExactRequiredFleet_ShouldReturnTrue() {
        Ship destroyer1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        Ship destroyer2 = Ship.createShip(new Coordinate(2, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        Ship carrier = Ship.createShip(new Coordinate(5, 5), Orientation.VERTICAL_DOWN, ShipType.CARRIER, grid);

        fleetManager.addShip(destroyer1);
        fleetManager.addShip(destroyer2);
        fleetManager.addShip(carrier);

        boolean result = fleetManager.isFleetComplete();

        assertTrue(result, "Expected fleet to be complete when all required ships are added.");
    }

    @Test
    void isFleetComplete_MissingShipType_ShouldReturnFalse() {
        Ship destroyer1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        Ship destroyer2 = Ship.createShip(new Coordinate(2, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);

        fleetManager.addShip(destroyer1);
        fleetManager.addShip(destroyer2);

        boolean result = fleetManager.isFleetComplete();

        assertFalse(result, "Expected fleet to be incomplete when a required ship type is missing.");
    }

    @Test
    void isFleetComplete_ExtraShipsAdded_ShouldReturnFalse() {
        Ship destroyer1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        Ship carrier = Ship.createShip(new Coordinate(5, 5), Orientation.VERTICAL_DOWN, ShipType.CARRIER, grid);

        fleetManager.addShip(destroyer1);
        fleetManager.addShip(carrier);

        boolean result = fleetManager.isFleetComplete();

        assertFalse(result, "Expected fleet to be incomplete when extra ships are added.");
    }

    @Test
    void isFleetComplete_OneShipOfEachRequiredType_ShouldReturnFalse() {
        Ship destroyer = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        Ship cruiser = Ship.createShip(new Coordinate(3, 3), Orientation.VERTICAL_DOWN, ShipType.CARRIER, grid);

        fleetManager.addShip(destroyer);
        fleetManager.addShip(cruiser);

        boolean result = fleetManager.isFleetComplete();

        assertFalse(result, "Expected fleet to be incomplete when the count of a required ship type is not met.");
    }

    @Test
    void testAddShip_SuccessWithinConfigLimit(){
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        assertTrue(fleetManager.addShip(ship));
        assertEquals(1, fleetManager.getFleet().size());
    }

    @Test
    void testAddShip_FailureExceedingConfigLimit(){
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.CARRIER, grid);
        assertTrue(fleetManager.addShip(ship));
        Ship ship2 = Ship.createShip(new Coordinate(8, 0), Orientation.HORIZONTAL_RIGHT, ShipType.CARRIER, grid);
        assertFalse(fleetManager.addShip(ship2));
        assertEquals(1, fleetManager.getFleet().size());
    }

    @Test
    void testAddShip_FailureShipTypeNotInConfig(){
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.FRIGATE, grid);
        assertFalse(fleetManager.addShip(ship));
    }

    @Test
    void testGetPlacedCounts_EmptyFleet() {
        Map<ShipType, Integer> counts = fleetManager.getPlacedCounts();
        assertTrue(counts.isEmpty(), "Expected empty map for empty fleet.");
    }

    @Test
    void testGetPlacedCounts_PartiallyPlaced() {
        Ship ship1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship1);

        Map<ShipType, Integer> counts = fleetManager.getPlacedCounts();
        assertEquals(1, counts.size());
        assertEquals(1, counts.get(ShipType.DESTROYER));
    }

    @Test
    void testGetPlacedCounts_MultipleShipTypes() {
        Ship ship1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        Ship ship2 = Ship.createShip(new Coordinate(2, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        Ship ship3 = Ship.createShip(new Coordinate(5, 5), Orientation.VERTICAL_DOWN, ShipType.CARRIER, grid);

        fleetManager.addShip(ship1);
        fleetManager.addShip(ship2);
        fleetManager.addShip(ship3);

        Map<ShipType, Integer> counts = fleetManager.getPlacedCounts();
        assertEquals(2, counts.size()); // DESTROYER and CARRIER
        assertEquals(2, counts.get(ShipType.DESTROYER));
        assertEquals(1, counts.get(ShipType.CARRIER));
    }
}
