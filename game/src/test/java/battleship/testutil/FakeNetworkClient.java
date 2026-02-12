package battleship.testutil;

import it.units.battleship.controller.game.network.AbstractPlayerCommunication;
import it.units.battleship.data.socket.GameMessageType;

/**
 * Fake implementation of AbstractPlayerCommunication for testing purposes.
 * Records all outgoing messages so tests can assert on them.
 */
public class FakeNetworkClient extends AbstractPlayerCommunication {
    public GameMessageType lastMessageType;
    public Object lastMessagePayload;
    public int messageCount;

    @Override
    public <T> void sendMessage(GameMessageType type, T payload) {
        lastMessageType = type;
        lastMessagePayload = payload;
        messageCount++;
    }
}
