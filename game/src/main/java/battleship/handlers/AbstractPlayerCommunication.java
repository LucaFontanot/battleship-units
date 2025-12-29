package battleship.handlers;

import it.units.battleship.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for player communication handling.
 */
public abstract class AbstractPlayerCommunication implements CommunicationEvents{
    final List<CommunicationEvents> communicationEventsListeners = new ArrayList<>();

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

    /**
     * Notifies all registered listeners when a player sends the coordinates of a shot.
     *
     * @param playerName the name of the player who performed the shot
     * @param shotCoordinates the coordinates of the shot provided by the player
     */
    public void onShotReceived(String playerName, Coordinate shotCoordinates){
        for (CommunicationEvents listener : communicationEventsListeners) {
            listener.onShotReceived(playerName, shotCoordinates);
        }
    }

    public abstract void sendMessage(String message);
    public abstract void sendShot(Coordinate shotCoordinates);
}
