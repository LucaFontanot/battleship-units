package ui;

import battleship.model.*;
import battleship.ui.grid.GridUI;
import it.units.battleship.Coordinate;
import it.units.battleship.Logger;
import it.units.battleship.service.PathManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.Map;

public class TestGridUI {

    @BeforeEach
    public void setup() {
        System.setProperty("programPath", PathManager.getProgramPath().resolve("logs").toAbsolutePath().normalize().toString());
        Logger.setDebugEnabled(true);
    }

    @Test
    public void testVisualUI() throws InterruptedException {
        Grid grid = new Grid(10, 10);
        Map<ShipType, Integer> requiredFleetConfiguration = Map.of(
                ShipType.DESTROYER, 1,
                ShipType.BATTLESHIP, 1,
                ShipType.CRUISER, 1,
                ShipType.FRIGATE, 1,
                ShipType.CARRIER, 1
        );
        FleetManager fleetManager = new FleetManager(grid, requiredFleetConfiguration);

        GridUI gridUI = new GridUI(fleetManager);
        javax.swing.JFrame frame = new javax.swing.JFrame("GridUI Test");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(gridUI);
        frame.pack();
        frame.setVisible(true);
        gridUI.reload();
        System.out.println("Initial Grid Displayed");
        Thread.sleep(2000);

        // Destroyer
        Ship ship1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship1);

        // Battleship
        Ship ship2 = Ship.createShip(new Coordinate(2, 2), Orientation.VERTICAL_DOWN, ShipType.BATTLESHIP, grid);
        fleetManager.addShip(ship2);

        // Cruiser
        Ship ship3 = Ship.createShip(new Coordinate(4, 4), Orientation.HORIZONTAL_RIGHT, ShipType.CRUISER, grid);
        fleetManager.addShip(ship3);

        // Frigate
        Ship ship4 = Ship.createShip(new Coordinate(7, 7), Orientation.VERTICAL_DOWN, ShipType.FRIGATE, grid);
        fleetManager.addShip(ship4);

        // Carrier
        Ship ship5 = Ship.createShip(new Coordinate(0, 5), Orientation.HORIZONTAL_RIGHT, ShipType.CARRIER, grid);
        fleetManager.addShip(ship5);

        System.out.println("Added all ships");

        ship1.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        System.out.println("Shot at Destroyer");
        Thread.sleep(2000);
        ship2.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        System.out.println("Shot at Battleship");
        Thread.sleep(2000);
        ship3.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        System.out.println("Shot at Cruiser");
        Thread.sleep(2000);
        ship4.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        System.out.println("Shot at Frigate");
        Thread.sleep(2000);
        ship5.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        System.out.println("Shot at Carrier");

        Thread.sleep(5000);
        frame.dispose();
    }

    @Test
    public void testDestroyer() throws InterruptedException {
        Grid grid = new Grid(15, 15);
        Map<ShipType, Integer> requiredFleetConfiguration = Map.of(
                ShipType.DESTROYER, 1
        );
        FleetManager fleetManager = new FleetManager(grid, requiredFleetConfiguration);

        GridUI gridUI = new GridUI(fleetManager);
        javax.swing.JFrame frame = new javax.swing.JFrame("GridUI Test");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(gridUI);
        frame.pack();
        frame.setVisible(true);
        gridUI.reload();

        Ship ship = Ship.createShip(new Coordinate(7, 7), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.HORIZONTAL_LEFT, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.VERTICAL_DOWN, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.VERTICAL_UP, ShipType.DESTROYER, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        frame.dispose();
    }

    @Test
    public void testBattleship() throws InterruptedException {
        Grid grid = new Grid(15, 15);
        Map<ShipType, Integer> requiredFleetConfiguration = Map.of(
                ShipType.BATTLESHIP, 1
        );
        FleetManager fleetManager = new FleetManager(grid, requiredFleetConfiguration);

        GridUI gridUI = new GridUI(fleetManager);
        javax.swing.JFrame frame = new javax.swing.JFrame("GridUI Battleship Test");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(gridUI);
        frame.pack();
        frame.setVisible(true);
        gridUI.reload();

        Ship ship = Ship.createShip(new Coordinate(7, 7), Orientation.HORIZONTAL_RIGHT, ShipType.BATTLESHIP, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.HORIZONTAL_LEFT, ShipType.BATTLESHIP, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.VERTICAL_DOWN, ShipType.BATTLESHIP, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.VERTICAL_UP, ShipType.BATTLESHIP, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        frame.dispose();
    }

    @Test
    public void testCruiser() throws InterruptedException {
        Grid grid = new Grid(15, 15);
        Map<ShipType, Integer> requiredFleetConfiguration = Map.of(
                ShipType.CRUISER, 1
        );
        FleetManager fleetManager = new FleetManager(grid, requiredFleetConfiguration);

        GridUI gridUI = new GridUI(fleetManager);
        javax.swing.JFrame frame = new javax.swing.JFrame("GridUI Cruiser Test");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(gridUI);
        frame.pack();
        frame.setVisible(true);
        gridUI.reload();

        Ship ship = Ship.createShip(new Coordinate(7, 7), Orientation.HORIZONTAL_RIGHT, ShipType.CRUISER, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.HORIZONTAL_LEFT, ShipType.CRUISER, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.VERTICAL_DOWN, ShipType.CRUISER, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.VERTICAL_UP, ShipType.CRUISER, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        frame.dispose();
    }

    @Test
    public void testFrigate() throws InterruptedException {
        Grid grid = new Grid(15, 15);
        Map<ShipType, Integer> requiredFleetConfiguration = Map.of(
                ShipType.FRIGATE, 1
        );
        FleetManager fleetManager = new FleetManager(grid, requiredFleetConfiguration);

        GridUI gridUI = new GridUI(fleetManager);
        javax.swing.JFrame frame = new javax.swing.JFrame("GridUI Frigate Test");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(gridUI);
        frame.pack();
        frame.setVisible(true);
        gridUI.reload();

        Ship ship = Ship.createShip(new Coordinate(7, 7), Orientation.HORIZONTAL_RIGHT, ShipType.FRIGATE, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.HORIZONTAL_LEFT, ShipType.FRIGATE, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.VERTICAL_DOWN, ShipType.FRIGATE, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.VERTICAL_UP, ShipType.FRIGATE, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        frame.dispose();
    }

    @Test
    public void testCarrier() throws InterruptedException {
        Grid grid = new Grid(15, 15);
        Map<ShipType, Integer> requiredFleetConfiguration = Map.of(
                ShipType.CARRIER, 1
        );
        FleetManager fleetManager = new FleetManager(grid, requiredFleetConfiguration);

        GridUI gridUI = new GridUI(fleetManager);
        javax.swing.JFrame frame = new javax.swing.JFrame("GridUI Carrier Test");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(gridUI);
        frame.pack();
        frame.setVisible(true);
        gridUI.reload();

        Ship ship = Ship.createShip(new Coordinate(7, 7), Orientation.HORIZONTAL_RIGHT, ShipType.CARRIER, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.HORIZONTAL_LEFT, ShipType.CARRIER, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.VERTICAL_DOWN, ShipType.CARRIER, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        fleetManager.removeShipByReference(ship);
        ship.getCoordinates().forEach(coord -> grid.changeState(coord, CellState.EMPTY));
        ship = Ship.createShip(new Coordinate(7, 7), Orientation.VERTICAL_UP, ShipType.CARRIER, grid);
        fleetManager.addShip(ship);
        ship.getCoordinates().forEach(coord -> fleetManager.handleIncomingShot(coord));
        gridUI.reload();
        Thread.sleep(2000);
        frame.dispose();
    }
}
