package battleship.controller;

import battleship.controller.handlers.AbstractPlayerCommunication;
import battleship.controller.handlers.CommunicationEvents;
import battleship.model.*;
import battleship.model.converter.GameDataMapper;
import battleship.view.GameView;
import it.units.battleship.*;
import battleship.model.Ship;
import it.units.battleship.data.socket.GameMessageType;
import it.units.battleship.data.socket.payloads.*;
import lombok.Getter;
import lombok.NonNull;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Orchestrates the game logic and coordinates communication between the game model,
 * the UI view, and the network communication layer.
 */
public class GameController implements CommunicationEvents, GridInteractionObserver {

    private final Grid grid;
    private final FleetManager fleetManager;
    @Getter
    private GameState gameState;
    private final GameView view;
    private final AbstractPlayerCommunication communication;

    public GameController(@NonNull Grid grid,@NonNull FleetManager fleetManager,@NonNull AbstractPlayerCommunication communication,@NonNull GameView view) {
        this.grid = grid;
        this.communication = communication;
        this.fleetManager = fleetManager;
        this.view = view;
        this.gameState = GameState.SETUP;
    }

    public void startGame(){
        List<Ship> currentFleet = fleetManager.getFleet();
        Map<ShipType, Integer> shipCounts = currentFleet.stream()
                .collect(Collectors.groupingBy(
                        Ship::getShipType,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        Map<ShipType, Integer> fleetConfiguration = fleetManager.getRequiredFleetConfiguration(); // Saranno tutti 0
        view.refreshFleetSelection(shipCounts, fleetConfiguration);

    }

    public void updatePlayerGrid(String gridSerialized, List<Ship> shipFleet) {
        view.updatePlayerGrid(gridSerialized, shipFleet);
    }

    @Override
    public void onPlayerMessage(String playerName, String message) {

    }

    @Override
    public void onOpponentGridUpdate(GridUpdateDTO gridUpdateDTO) {
        Logger.log("Grid update");

        List<ShipDTO> fleetDTO = gridUpdateDTO.fleet();

        List<Ship> fleet = GameDataMapper.toShipList(fleetDTO);

        view.updateOpponentGrid(gridUpdateDTO.gridSerialized(), fleet);
    }
    @Override
    public void onShotReceived(ShotRequestDTO shotRequestDTO) {
        Logger.log("Shot received");

        Coordinate shotCoord = shotRequestDTO.coord();

        boolean shotOutcome = fleetManager.handleIncomingShot(shotCoord);

        List<Ship> fleet = fleetManager.getFleet();
        List<Ship> sunkShipFleet = fleet.stream().filter(ship -> ship.isSunk()).collect(Collectors.toList());
        List<ShipDTO> fleetDTO = GameDataMapper.toShipDTO(sunkShipFleet);
        String gridSerialized = grid.gridSerialization();

        GridUpdateDTO gridUpdateDTO = new GridUpdateDTO(shotOutcome, gridSerialized, fleetDTO);

        communication.sendMessage(GameMessageType.GRID_UPDATE, gridUpdateDTO);

        updatePlayerGrid(gridSerialized, fleet);
    }

    @Override
    public void onGameSetupReceived(GameConfigDTO gameConfigDTO) {
    }

    @Override
    public void onGridHover(Coordinate coordinate) {
        switch (gameState){
            case SETUP -> {
                Orientation selectedOrientation = view.getSelectedOrientation();
                ShipType selectedShipType = view.getSelectedShipType();

                if (selectedShipType == null) return;
                try {
                    Ship ship = Ship.createShip(coordinate, selectedOrientation, selectedShipType, fleetManager.getGrid());
                    boolean valid = fleetManager.canPlaceShip(ship);

                    view.showPlacementPreview(ship.getCoordinates(), valid, ship);

                } catch (IllegalArgumentException ex) {
                    LinkedHashSet<Coordinate> coords = selectedShipType.getShipCoordinates(coordinate, selectedOrientation);
                    view.showPlacementPreview(coords, false, null);
                }
        }
        }
    }

    @Override
    public void onGridClick(Coordinate coordinate) {
        switch (gameState){
            case SETUP -> {
                Orientation selectedOrientation = view.getSelectedOrientation();
                ShipType selectedShipType = view.getSelectedShipType();

                if (selectedShipType == null) return;

                try {
                    Ship ship = Ship.createShip(coordinate, selectedOrientation, selectedShipType, fleetManager.getGrid());
                    boolean placed = fleetManager.addShip(ship);

                    if (placed) {
                        List<Ship> currentFleet = fleetManager.getFleet();

                        view.updatePlayerGrid(grid.gridSerialization(), currentFleet);

                        Map<ShipType, Integer> shipCounts = currentFleet.stream()
                                .collect(Collectors.groupingBy(
                                        Ship::getShipType,
                                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                                ));
                        Map<ShipType, Integer> fleetConfiguration = fleetManager.getRequiredFleetConfiguration();

                        view.refreshFleetSelection(shipCounts, fleetConfiguration);
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                        view.showPlacementPreview(ship.getCoordinates(), false, ship);
                    }
                } catch (IllegalArgumentException ex) {
                    Toolkit.getDefaultToolkit().beep();

                    LinkedHashSet<Coordinate> coords = selectedShipType.getShipCoordinates(coordinate,selectedOrientation);
                    view.showPlacementPreview(coords, false, null);
                }
            }
        }
    }
}