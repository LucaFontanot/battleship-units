package battleship.controller.handlers.network;

import battleship.controller.actions.NetworkOutputActions;
import battleship.controller.network.AbstractPlayerCommunication;
import battleship.model.Grid;
import battleship.model.Ship;
import battleship.model.converter.GameDataMapper;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.data.socket.GameMessageType;
import it.units.battleship.data.socket.payloads.GameStatusDTO;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;
import lombok.NonNull;

import java.util.List;

public class NetworkOutputHandler implements NetworkOutputActions {
    private final AbstractPlayerCommunication communication;

    public NetworkOutputHandler(AbstractPlayerCommunication communication) {
        this.communication = communication;
    }


    @Override
    public void sendGameStatus(@NonNull GameState gameState, String message) {
        GameStatusDTO gameStatusDTO = new GameStatusDTO(gameState, message);
        communication.sendMessage(GameMessageType.TURN_CHANGE, gameStatusDTO);
    }

    @Override
    public void sendShotRequest(Coordinate coordinate) {
        ShotRequestDTO shotRequestDTO = new ShotRequestDTO(coordinate);
        communication.sendMessage(GameMessageType.SHOT_REQUEST, shotRequestDTO);
    }

    @Override
    public void sendGridUpdate(Grid grid, List<Ship> fleet, boolean shotOutcome) {
        GridUpdateDTO gridUpdateDTO = GameDataMapper.toGridUpdateDTO(shotOutcome, grid, fleet);
        communication.sendMessage(GameMessageType.GRID_UPDATE, gridUpdateDTO);
    }
}
