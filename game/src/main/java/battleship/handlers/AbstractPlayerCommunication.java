package battleship.handlers;

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
}
