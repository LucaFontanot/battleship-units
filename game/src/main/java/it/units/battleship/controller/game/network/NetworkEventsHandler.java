package it.units.battleship.controller.game.network;

import it.units.battleship.controller.game.actions.NetworkActionsReceiver;
import it.units.battleship.controller.game.events.CommunicationEvents;
import it.units.battleship.model.Ship;
import it.units.battleship.serializer.GameDataMapper;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Logger;
import it.units.battleship.data.socket.payloads.GameConfigDTO;
import it.units.battleship.data.socket.payloads.GameStatusDTO;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;

import java.util.List;

public class NetworkEventsHandler implements CommunicationEvents {

    private final NetworkActionsReceiver networkActions;

    public NetworkEventsHandler(NetworkActionsReceiver networkActions) {
        this.networkActions = networkActions;
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
    public void onGameStatusReceived(GameStatusDTO gameStatusDTO) {
        GameState gameState = GameDataMapper.toGameState(gameStatusDTO);
        String message = GameDataMapper.toMessage(gameStatusDTO);
        networkActions.processGameStatusUpdate(gameState, message);
    }
}
