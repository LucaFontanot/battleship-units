package battleship.controller;

import battleship.handlers.AbstractPlayerCommunication;
import battleship.handlers.CommunicationEvents;
import battleship.model.*;
import battleship.model.converter.GameDataMapper;
import battleship.view.GameView;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import battleship.model.Ship;
import it.units.battleship.Logger;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShipDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Orchestrates the game logic and coordinates communication between the game model,
 * the UI view, and the network communication layer.
 */
public class GameController implements CommunicationEvents {

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

        communication.sendMessage("grid_update", gridUpdateDTO);

        updatePlayerGrid(gridSerialized, fleet);
    }
}