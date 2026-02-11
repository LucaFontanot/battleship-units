package battleship.model.converter;

import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.serializer.GameDataMapper;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import it.units.battleship.data.socket.payloads.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameDataMapper {

    @Test
    void testToShipList_WithValidDTOs() {
        LinkedHashSet<Coordinate> coords1 = new LinkedHashSet<>();
        coords1.add(new Coordinate(0, 0));
        coords1.add(new Coordinate(0, 1));

        LinkedHashSet<Coordinate> coords2 = new LinkedHashSet<>();
        coords2.add(new Coordinate(2, 2));
        coords2.add(new Coordinate(3, 2));
        coords2.add(new Coordinate(4, 2));

        List<ShipDTO> dtos = List.of(
            new ShipDTO(ShipType.DESTROYER, coords1, Orientation.HORIZONTAL_RIGHT),
            new ShipDTO(ShipType.FRIGATE, coords2, Orientation.VERTICAL_DOWN)
        );

        List<Ship> ships = GameDataMapper.toShipList(dtos);

        assertNotNull(ships);
        assertEquals(2, ships.size());
        assertEquals(ShipType.DESTROYER, ships.get(0).getShipType());
        assertEquals(ShipType.FRIGATE, ships.get(1).getShipType());
        assertEquals(coords1, ships.get(0).getCoordinates());
        assertEquals(coords2, ships.get(1).getCoordinates());
    }

    @Test
    void testToShipList_WithEmptyList() {
        List<Ship> ships = GameDataMapper.toShipList(new ArrayList<>());

        assertNotNull(ships);
        assertTrue(ships.isEmpty());
    }

    @Test
    void testToShip_ValidDTO() {
        LinkedHashSet<Coordinate> coords = new LinkedHashSet<>();
        coords.add(new Coordinate(1, 1));
        coords.add(new Coordinate(1, 2));
        coords.add(new Coordinate(1, 3));
        coords.add(new Coordinate(1, 4));
        coords.add(new Coordinate(1, 5));

        ShipDTO dto = new ShipDTO(ShipType.BATTLESHIP, coords, Orientation.HORIZONTAL_RIGHT);
        Ship ship = GameDataMapper.toShip(dto);

        assertNotNull(ship);
        assertEquals(ShipType.BATTLESHIP, ship.getShipType());
        assertEquals(coords, ship.getCoordinates());
        assertEquals(Orientation.HORIZONTAL_RIGHT, ship.getOrientation());
        assertEquals(5, ship.getSize());
    }

    @Test
    void testToShipDTO_WithValidShips() {
        Grid grid = new Grid(10, 10);
        Ship ship1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        Ship ship2 = Ship.createShip(new Coordinate(2, 2), Orientation.VERTICAL_DOWN, ShipType.FRIGATE, grid);

        List<Ship> fleet = List.of(ship1, ship2);

        List<ShipDTO> dtos = GameDataMapper.toShipDTO(fleet);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals(ShipType.DESTROYER, dtos.get(0).type());
        assertEquals(ShipType.FRIGATE, dtos.get(1).type());
        assertEquals(ship1.getCoordinates(), dtos.get(0).coordinates());
        assertEquals(ship2.getCoordinates(), dtos.get(1).coordinates());
        assertEquals(Orientation.HORIZONTAL_RIGHT, dtos.get(0).orientation());
        assertEquals(Orientation.VERTICAL_DOWN, dtos.get(1).orientation());
    }

    @Test
    void testToShipDTO_WithEmptyFleet() {
        List<ShipDTO> dtos = GameDataMapper.toShipDTO(new ArrayList<>());

        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    void testToGridUpdateDTO_WithHit() {
        Grid grid = new Grid(10, 10);

        Map<ShipType, Integer> shipCounts = Map.of(ShipType.DESTROYER, 1, ShipType.FRIGATE, 1);

        FleetManager fleetManager = new FleetManager(grid,shipCounts);
        Ship ship1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        Ship ship2 = Ship.createShip(new Coordinate(2, 2), Orientation.VERTICAL_DOWN, ShipType.FRIGATE, grid);

        fleetManager.addShip(ship1);
        fleetManager.addShip(ship2);

        fleetManager.handleIncomingShot(new Coordinate(0, 0));
        fleetManager.handleIncomingShot(new Coordinate(0, 1));

        List<Ship> fleet = fleetManager.getFleet();

        GridUpdateDTO dto = GameDataMapper.toGridUpdateDTO(true, grid, fleet);

        assertNotNull(dto);
        assertTrue(dto.shotOutcome());
        assertNotNull(dto.gridSerialized());
        assertNotNull(dto.fleet());
        assertEquals(1, dto.fleet().size()); // Only sunk ships
        assertEquals(ShipType.DESTROYER, dto.fleet().get(0).type());
    }

    @Test
    void testToGridUpdateDTO_WithMiss() {
        Grid grid = new Grid(10, 10);

        Map<ShipType, Integer> shipCounts = Map.of(ShipType.DESTROYER, 1);

        FleetManager fleetManager = new FleetManager(grid, shipCounts);
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);

        fleetManager.addShip(ship);
        fleetManager.handleIncomingShot(new Coordinate(5, 5));

        List<Ship> fleet = fleetManager.getFleet();

        GridUpdateDTO dto = GameDataMapper.toGridUpdateDTO(false, grid, fleet);

        assertNotNull(dto);
        assertFalse(dto.shotOutcome());
        assertNotNull(dto.gridSerialized());
        assertNotNull(dto.fleet());
        assertTrue(dto.fleet().isEmpty()); // No sunk ships
    }

    @Test
    void testToGridUpdateDTO_WithMultipleSunkShips() {
        Grid grid = new Grid(10, 10);
        FleetManager fleetManager = new FleetManager(grid, Map.of(ShipType.DESTROYER, 2, ShipType.FRIGATE, 1));

        Ship ship1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        Ship ship2 = Ship.createShip(new Coordinate(2, 2), Orientation.VERTICAL_DOWN, ShipType.FRIGATE, grid);
        Ship ship3 = Ship.createShip(new Coordinate(5, 5), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);

        fleetManager.addShip(ship1);
        fleetManager.addShip(ship2);
        fleetManager.addShip(ship3);

        fleetManager.getShipByReference(ship1).getCoordinates().forEach(fleetManager::handleIncomingShot);
        fleetManager.getShipByReference(ship2).getCoordinates().forEach(fleetManager::handleIncomingShot);

        fleetManager.handleIncomingShot(new Coordinate(5, 5));

        List<Ship> fleet = fleetManager.getFleet();

        GridUpdateDTO dto = GameDataMapper.toGridUpdateDTO(true, grid, fleet);

        assertNotNull(dto);
        assertTrue(dto.shotOutcome());
        assertNotNull(dto.fleet());
        assertEquals(2, dto.fleet().size()); // Only ship1 and ship2 are sunk
    }

    @Test
    void testToShip_PreservesCoordinateOrder() {
        LinkedHashSet<Coordinate> coords = new LinkedHashSet<>();
        coords.add(new Coordinate(3, 3));
        coords.add(new Coordinate(3, 4));
        coords.add(new Coordinate(3, 5));
        coords.add(new Coordinate(3, 6));
        coords.add(new Coordinate(3, 7));
        coords.add(new Coordinate(3, 8));
        coords.add(new Coordinate(3, 9));

        ShipDTO dto = new ShipDTO(ShipType.CARRIER, coords, Orientation.HORIZONTAL_RIGHT);

        Ship ship = GameDataMapper.toShip(dto);

        assertNotNull(ship);
        assertEquals(coords, ship.getCoordinates());
        List<Coordinate> coordsList = new ArrayList<>(ship.getCoordinates());
        List<Coordinate> expectedList = new ArrayList<>(coords);
        assertEquals(expectedList, coordsList);
    }

    @Test
    void testRoundTrip_ShipToDTO_AndBack() {
        Grid grid = new Grid(10, 10);
        Ship originalShip = Ship.createShip(new Coordinate(1, 1), Orientation.VERTICAL_DOWN, ShipType.BATTLESHIP, grid);

        List<ShipDTO> dtos = GameDataMapper.toShipDTO(List.of(originalShip));

        List<Ship> ships = GameDataMapper.toShipList(dtos);

        assertNotNull(ships);
        assertEquals(1, ships.size());
        Ship reconstructedShip = ships.get(0);

        assertEquals(originalShip.getShipType(), reconstructedShip.getShipType());
        assertEquals(originalShip.getCoordinates(), reconstructedShip.getCoordinates());
        assertEquals(originalShip.getOrientation(), reconstructedShip.getOrientation());
        assertEquals(originalShip.getSize(), reconstructedShip.getSize());
    }

    @Test
    void testToGameConfigDTO() {
        Map<ShipType, Integer> fleetRules = Map.of(ShipType.BATTLESHIP, 1, ShipType.DESTROYER, 2);
        GameConfigDTO dto = GameDataMapper.toGameConfigDTO(10, 10, fleetRules);

        assertNotNull(dto);
        assertEquals(10, dto.rows());
        assertEquals(10, dto.cols());
        assertEquals(fleetRules, dto.fleetRules());
    }

    @Test
    void testToGameStatusDTO() {
        GameStatusDTO dto = GameDataMapper.toGameStatusDTO(GameState.ACTIVE_TURN, "Your turn!");

        assertNotNull(dto);
        assertEquals(GameState.ACTIVE_TURN, dto.state());
        assertEquals("Your turn!", dto.message());
    }

    @Test
    void testToShotRequestDTO() {
        Coordinate coord = new Coordinate(3, 4);
        ShotRequestDTO dto = GameDataMapper.toShotRequestDTO(coord);

        assertNotNull(dto);
        assertEquals(coord, dto.coord());
    }

    @Test
    void testToCoordinate() {
        Coordinate coord = new Coordinate(5, 6);
        ShotRequestDTO dto = new ShotRequestDTO(coord);
        Coordinate extracted = GameDataMapper.toCoordinate(dto);
        assertEquals(coord, extracted);
    }

    @Test
    void testToGameState() {
        GameStatusDTO dto = new GameStatusDTO(GameState.WAITING_FOR_OPPONENT, "Waiting...");
        assertEquals(GameState.WAITING_FOR_OPPONENT, GameDataMapper.toGameState(dto));
    }

    @Test
    void testToMessage() {
        GameStatusDTO dto = new GameStatusDTO(GameState.ACTIVE_TURN, "Msg");
        assertEquals("Msg", GameDataMapper.toMessage(dto));
        assertNull(GameDataMapper.toMessage(null));
    }

    @Test
    void testToFleetRules() {
        Map<ShipType, Integer> rules = Map.of(ShipType.BATTLESHIP, 3);
        GameConfigDTO dto = new GameConfigDTO(10, 10, rules);
        assertEquals(rules, GameDataMapper.toFleetRules(dto));
    }

    @Test
    void testToRowsAndCols() {
        GameConfigDTO dto = new GameConfigDTO(8, 12, Map.of());
        assertEquals(8, GameDataMapper.toRows(dto));
        assertEquals(12, GameDataMapper.toCols(dto));
    }

    @Test
    void testGridUpdateExtraction() {
        Ship ship = Ship.createShip(new Coordinate(0,0), Orientation.HORIZONTAL_RIGHT, ShipType.FRIGATE, new Grid(10,10));
        LinkedHashSet<Coordinate> coords = ship.getCoordinates();
        ShipDTO shipDTO = new ShipDTO(ShipType.FRIGATE, coords, Orientation.HORIZONTAL_RIGHT);
        GridUpdateDTO dto = new GridUpdateDTO(true, "grid_str", List.of(shipDTO));

        assertEquals(true, GameDataMapper.toShotOutcome(dto));
        assertEquals("grid_str", GameDataMapper.toGridSerialized(dto));

        List<Ship> extractedShips = GameDataMapper.toShipList(dto);
        assertEquals(1, extractedShips.size());
        assertEquals(ShipType.FRIGATE, extractedShips.get(0).getShipType());
    }

    @Test
    void testToShipDTO_WithNullFleet_ThrowsException() {
        assertThrows(NullPointerException.class,
                () -> GameDataMapper.toShipDTO(null));
    }

    @Test
    void testToShipList_WithNullFleet_ThrowsException() {
        assertThrows(NullPointerException.class,
                () -> GameDataMapper.toShipList((List<ShipDTO>) null));
    }

    @Test
    void testToShip_WithNullDTO_ThrowsException() {
        assertThrows(NullPointerException.class,
                () -> GameDataMapper.toShip(null));
    }

    @Test
    void testToGridUpdateDTO_WithNullGrid_ThrowsException() {
        assertThrows(NullPointerException.class,
                () -> GameDataMapper.toGridUpdateDTO(true, null, List.of()));
    }

    @Test
    void testToGridUpdateDTO_WithNullFleet_ThrowsException() {
        Grid grid = new Grid(5, 5);
        assertThrows(NullPointerException.class,
                () -> GameDataMapper.toGridUpdateDTO(true, grid, null));
    }

    @Test
    void testToGameConfigDTO_WithNullFleetConfiguration_ThrowsException() {
        assertThrows(NullPointerException.class,
                () -> GameDataMapper.toGameConfigDTO(10, 10, null));
    }

    @Test
    void testToGameStatusDTO_WithNullState_ThrowsException() {
        assertThrows(NullPointerException.class,
                () -> GameDataMapper.toGameStatusDTO(null, "Msg"));
    }

    @Test
    void testToShotRequestDTO_WithNullCoordinate_ThrowsException() {
        assertThrows(NullPointerException.class,
                () -> GameDataMapper.toShotRequestDTO(null));
    }
}
