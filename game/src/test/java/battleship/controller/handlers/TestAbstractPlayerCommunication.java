package battleship.controller.handlers;

import battleship.controller.game.AbstractPlayerCommunication;
import battleship.controller.game.handlers.CommunicationEvents;
import it.units.battleship.GameState;
import it.units.battleship.data.socket.GameMessageType;
import it.units.battleship.data.socket.payloads.GameConfigDTO;
import it.units.battleship.data.socket.payloads.GameStatusDTO;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;
import it.units.battleship.Coordinate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class TestAbstractPlayerCommunication {

    private AbstractPlayerCommunication communication;
    private CommunicationEvents mockListener1;
    private CommunicationEvents mockListener2;

    // Concrete implementation for testing purposes
    private static class ConcretePlayerCommunication extends AbstractPlayerCommunication {
        @Override
        public <T> void sendMessage(GameMessageType type, T payload) {
            // No-op for abstract test
        }
    }

    @BeforeEach
    void setUp() {
        communication = new ConcretePlayerCommunication();
        mockListener1 = mock(CommunicationEvents.class);
        mockListener2 = mock(CommunicationEvents.class);
    }

    @Test
    void testAddAndNotifyPlayerMessage() {
        communication.addCommunicationEventsListener(mockListener1);
        communication.onPlayerMessage("Player1", "Hello");

        verify(mockListener1).onPlayerMessage("Player1", "Hello");
    }

    @Test
    void testMultipleListenersNotification() {
        communication.addCommunicationEventsListener(mockListener1);
        communication.addCommunicationEventsListener(mockListener2);
        
        communication.onPlayerMessage("Player1", "Hello");

        verify(mockListener1).onPlayerMessage("Player1", "Hello");
        verify(mockListener2).onPlayerMessage("Player1", "Hello");
    }

    @Test
    void testRemoveListener() {
        communication.addCommunicationEventsListener(mockListener1);
        communication.removeCommunicationEventsListener(mockListener1);
        
        communication.onPlayerMessage("Player1", "Hello");

        verify(mockListener1, never()).onPlayerMessage(anyString(), anyString());
    }

    @Test
    void testOnOpponentGridUpdate() {
        communication.addCommunicationEventsListener(mockListener1);
        GridUpdateDTO dto = new GridUpdateDTO(true, "grid", List.of());
        
        communication.onOpponentGridUpdate(dto);

        verify(mockListener1).onOpponentGridUpdate(dto);
    }

    @Test
    void testOnShotReceived() {
        communication.addCommunicationEventsListener(mockListener1);
        ShotRequestDTO dto = new ShotRequestDTO(new Coordinate(1, 1));
        
        communication.onShotReceived(dto);

        verify(mockListener1).onShotReceived(dto);
    }

    @Test
    void testOnGameSetupReceived() {
        communication.addCommunicationEventsListener(mockListener1);
        GameConfigDTO dto = new GameConfigDTO(10, 10, Map.of());
        
        communication.onGameSetupReceived(dto);

        verify(mockListener1).onGameSetupReceived(dto);
    }

    @Test
    void testOnGameStatusReceived() {
        communication.addCommunicationEventsListener(mockListener1);
        GameStatusDTO dto = new GameStatusDTO(GameState.ACTIVE_TURN, "Message");
        
        communication.onGameStatusReceived(dto);

        verify(mockListener1).onGameStatusReceived(dto);
    }
}
