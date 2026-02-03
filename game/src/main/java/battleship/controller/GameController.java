package battleship.controller;

import battleship.handlers.AbstractPlayerCommunication;
import battleship.handlers.CommunicationEvents;
import battleship.model.*;
import battleship.model.converter.GameDataMapper;
import battleship.view.GameView;
import it.units.battleship.*;
import battleship.model.Ship;
import it.units.battleship.data.socket.GameMessageType;
import it.units.battleship.data.socket.payloads.*;
import lombok.Getter;
import lombok.NonNull;

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
    public void onShipPlacement(Coordinate coordinate, ShipType shipType, Orientation orientation) {
        try {
            Ship ship = Ship.createShip(coordinate, orientation, shipType, fleetManager.getGrid());
            boolean valid = fleetManager.canPlaceShip(ship);

            view.showPlacementPreview(ship.getCoordinates(), valid, ship);

        } catch (IllegalArgumentException ex) {
            LinkedHashSet<Coordinate> coords = shipType.getShipCoordinates(coordinate, orientation);
            view.showPlacementPreview(coords, false, null);
        }
    }

    @Override
    public void onShipPlacementExit() {

    }
}