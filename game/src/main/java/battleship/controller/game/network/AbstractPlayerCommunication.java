package battleship.controller.game.network;

import battleship.controller.game.actions.NetworkOutputActions;
import battleship.controller.game.events.CommunicationEvents;
import battleship.model.game.Grid;
import battleship.model.game.Ship;
import battleship.serializer.GameDataMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Logger;
import it.units.battleship.data.socket.GameMessageType;
import it.units.battleship.data.socket.WebSocketMessage;
import it.units.battleship.data.socket.payloads.GameConfigDTO;
import it.units.battleship.data.socket.payloads.GameStatusDTO;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for player communication handling.
 */
public abstract class AbstractPlayerCommunication implements CommunicationEvents, NetworkOutputActions {
    final List<CommunicationEvents> communicationEventsListeners = new ArrayList<>();
    final Gson gson = new Gson();


    /**
     * Adds a listener for communication events.
     * @param listener the listener to add
     */
    public void addCommunicationEventsListener(CommunicationEvents listener) {
        communicationEventsListeners.add(listener);
    }

    /**
     * Removes a listener for communication events.
     * @param listener the listener to remove
     */
    public void removeCommunicationEventsListener(CommunicationEvents listener) {
        communicationEventsListeners.remove(listener);
    }

    /**
     * Notifies listeners of a player message.
     * @param playerName the name of the player
     * @param message the message sent by the player
     */
    public void onPlayerMessage(String playerName, String message){
        for (CommunicationEvents listener : communicationEventsListeners) {
            listener.onPlayerMessage(playerName, message);
        }
    }

    public void onOpponentGridUpdate(GridUpdateDTO gridUpdateDTO){
        for (CommunicationEvents listener : communicationEventsListeners) {
            listener.onOpponentGridUpdate(gridUpdateDTO);
        }
    }

    public void onShotReceived(ShotRequestDTO shotRequestDTO){
        for (CommunicationEvents listener : communicationEventsListeners) {
            listener.onShotReceived(shotRequestDTO);
        }
    }

    public void onGameSetupReceived(GameConfigDTO gameConfigDTO){
        for (CommunicationEvents listener : communicationEventsListeners) {
            listener.onGameSetupReceived(gameConfigDTO);
        }
    }

    public void onGameStatusReceived(GameStatusDTO gameStatusDTO){
        for (CommunicationEvents listener : communicationEventsListeners) {
            listener.onGameStatusReceived(gameStatusDTO);
        }
    }

    public abstract <T> void sendMessage(GameMessageType type, T payload);

    @Override
    public void sendGameStatus(@NonNull GameState gameState, String message) {
        GameStatusDTO gameStatusDTO = GameDataMapper.toGameStatusDTO(gameState, message);
        sendMessage(GameMessageType.TURN_CHANGE, gameStatusDTO);
    }

    @Override
    public void sendShotRequest(Coordinate coordinate) {
        ShotRequestDTO shotRequestDTO = GameDataMapper.toShotRequestDTO(coordinate);
        sendMessage(GameMessageType.SHOT_REQUEST, shotRequestDTO);
    }

    @Override
    public void sendGridUpdate(Grid grid, List<Ship> fleet, boolean shotOutcome) {
        GridUpdateDTO gridUpdateDTO = GameDataMapper.toGridUpdateDTO(shotOutcome, grid, fleet);
        sendMessage(GameMessageType.GRID_UPDATE, gridUpdateDTO);
    }
}
