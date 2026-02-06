package battleship.controller.handlers.network;

import battleship.controller.actions.GameNetworkActions;
import battleship.controller.network.CommunicationEvents;
import battleship.model.Ship;
import battleship.model.converter.GameDataMapper;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Logger;
import it.units.battleship.data.socket.payloads.*;

import java.util.List;

public class NetworkInputHandler implements CommunicationEvents {

    private final GameNetworkActions networkActions;

    public NetworkInputHandler(GameNetworkActions networkActions) {
        this.networkActions = networkActions;
    }


    @Override
    public void onPlayerMessage(String playerName, String message) {

    }

    @Override
    public void onOpponentGridUpdate(GridUpdateDTO gridUpdateDTO) {
        Logger.log("Grid update");

        List<ShipDTO> fleetDTO = gridUpdateDTO.fleet();

        List<Ship> fleet = GameDataMapper.toShipList(fleetDTO);

        networkActions.processOpponentGridUpdate(gridUpdateDTO.gridSerialized(), fleet);
    }

    @Override
    public void onShotReceived(ShotRequestDTO shotRequestDTO) {
        Logger.log("Shot received");

        Coordinate shotCoord = shotRequestDTO.coord();
        networkActions.processIncomingShot(shotCoord);
    }

    @Override
    public void onGameSetupReceived(GameConfigDTO gameConfigDTO) {

    }

    @Override
    public void onGameStatusReceived(GameStatusDTO gameStatusDTO) {
        GameState gameState = gameStatusDTO.state();
        String message = gameStatusDTO.message();
        networkActions.processGameStatusUpdate(gameState, message);
    }
}
