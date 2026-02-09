package battleship.controller.game.network;

import battleship.controller.game.actions.NetworkInputActions;
import battleship.controller.game.events.CommunicationEvents;
import battleship.model.Ship;
import battleship.serializer.GameDataMapper;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Logger;
import it.units.battleship.data.socket.payloads.*;

import java.util.List;

public class NetworkEventsHandler implements CommunicationEvents {

    private final NetworkInputActions networkActions;

    public NetworkEventsHandler(NetworkInputActions networkActions) {
        this.networkActions = networkActions;
    }


    @Override
    public void onPlayerMessage(String playerName, String message) {

    }

    @Override
    public void onOpponentGridUpdate(GridUpdateDTO gridUpdateDTO) {
        Logger.log("Grid update");

        List<Ship> fleet = GameDataMapper.toShipList(gridUpdateDTO);
        String gridSerialized = GameDataMapper.toGridSerialized(gridUpdateDTO);

        networkActions.processOpponentGridUpdate(gridSerialized, fleet);
    }

    @Override
    public void onShotReceived(ShotRequestDTO shotRequestDTO) {
        Logger.log("Shot received");

        Coordinate shotCoord = GameDataMapper.toCoordinate(shotRequestDTO);
        networkActions.processIncomingShot(shotCoord);
    }

    @Override
    public void onGameSetupReceived(GameConfigDTO gameConfigDTO) {

    }

    @Override
    public void onGameStatusReceived(GameStatusDTO gameStatusDTO) {
        GameState gameState = GameDataMapper.toGameState(gameStatusDTO);
        String message = GameDataMapper.toMessage(gameStatusDTO);
        networkActions.processGameStatusUpdate(gameState, message);
    }
}
