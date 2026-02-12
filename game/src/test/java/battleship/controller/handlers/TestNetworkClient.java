package battleship.controller.handlers;

import it.units.battleship.controller.game.events.CommunicationEvents;
import it.units.battleship.controller.game.network.NetworkClient;
import com.google.gson.Gson;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import it.units.battleship.data.LobbyData;
import it.units.battleship.data.socket.GameMessageType;
import it.units.battleship.data.socket.WebSocketMessage;
import it.units.battleship.data.socket.payloads.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestNetworkClient {

    private NetworkClient networkClient;
    private Gson gson;
    private MockCommunicationEventsListener mockListener;

    @BeforeEach
    void setUp() throws URISyntaxException {
        networkClient = new NetworkClient(new LobbyData(), "Player1");
        gson = new Gson();
        mockListener = new MockCommunicationEventsListener();
        networkClient.addCommunicationEventsListener(mockListener);
    }

    @Test
    void testAddCommunicationEventsListener() {
        NetworkClient client = assertDoesNotThrow(() -> new NetworkClient(new LobbyData(), "Player1"));
        MockCommunicationEventsListener listener = new MockCommunicationEventsListener();

        assertDoesNotThrow(() -> client.addCommunicationEventsListener(listener));
    }

    @Test
    void testRemoveCommunicationEventsListener() {
        MockCommunicationEventsListener listener = new MockCommunicationEventsListener();
        networkClient.addCommunicationEventsListener(listener);

        assertDoesNotThrow(() -> networkClient.removeCommunicationEventsListener(listener));
    }

    @Test
    void testSendMessage_ClientNotConnected() {
        ShotRequestDTO payload = new ShotRequestDTO(new Coordinate(1, 1));

        assertDoesNotThrow(() -> networkClient.sendMessage(GameMessageType.SHOT_REQUEST, payload));
    }

    /**
     * Tests that incoming GRID_UPDATE messages are correctly parsed and listeners are notified.
     */
    @Test
    void testHandleIncomingMessage_GridUpdate() throws Exception {
        List<ShipDTO> sunkShips = List.of(
            new ShipDTO(ShipType.DESTROYER,
                new LinkedHashSet<>(List.of(new Coordinate(0, 0), new Coordinate(0, 1))),
                Orientation.HORIZONTAL_RIGHT)
        );
        GridUpdateDTO gridUpdate = new GridUpdateDTO(true, "grid_serialized_data", sunkShips);
        WebSocketMessage<GridUpdateDTO> message = new WebSocketMessage<>(
            GameMessageType.GRID_UPDATE.getType(),
            gridUpdate
        );
        String json = gson.toJson(message);

        java.lang.reflect.Method method = NetworkClient.class.getDeclaredMethod("handleIncomingMessage", String.class);
        method.setAccessible(true);
        method.invoke(networkClient, json);

        assertTrue(mockListener.gridUpdateReceived);
        assertNotNull(mockListener.lastGridUpdate);
        assertTrue(mockListener.lastGridUpdate.shotOutcome());
        assertEquals(1, mockListener.lastGridUpdate.fleet().size());
    }

    /**
     * Tests that incoming SHOT_REQUEST messages are correctly parsed and listeners are notified.
     */
    @Test
    void testHandleIncomingMessage_ShotRequest() throws Exception {
        ShotRequestDTO shotRequest = new ShotRequestDTO(new Coordinate(3, 4));
        WebSocketMessage<ShotRequestDTO> message = new WebSocketMessage<>(
            GameMessageType.SHOT_REQUEST.getType(),
            shotRequest
        );
        String json = gson.toJson(message);

        java.lang.reflect.Method method = NetworkClient.class.getDeclaredMethod("handleIncomingMessage", String.class);
        method.setAccessible(true);
        method.invoke(networkClient, json);

        assertTrue(mockListener.shotRequestReceived);
        assertNotNull(mockListener.lastShotRequest);
        assertEquals(new Coordinate(3, 4), mockListener.lastShotRequest.coord());
    }

    /**
     * Tests that incoming TURN_CHANGE messages are correctly parsed and listeners are notified.
     */
    @Test
    void testHandleIncomingMessage_TurnChange() throws Exception {
        GameStatusDTO gameStatus = new GameStatusDTO(GameState.ACTIVE_TURN, "Your turn");
        WebSocketMessage<GameStatusDTO> message = new WebSocketMessage<>(
            GameMessageType.TURN_CHANGE.getType(),
            gameStatus
        );
        String json = gson.toJson(message);

        java.lang.reflect.Method method = NetworkClient.class.getDeclaredMethod("handleIncomingMessage", String.class);
        method.setAccessible(true);
        method.invoke(networkClient, json);

        assertTrue(mockListener.gameStatusReceived);
        assertNotNull(mockListener.lastGameStatus);
        assertEquals(GameState.ACTIVE_TURN, mockListener.lastGameStatus.state());
        assertEquals("Your turn", mockListener.lastGameStatus.message());
    }

    /**
     * Tests that unknown message types are handled gracefully without throwing exceptions.
     */
    @Test
    void testHandleIncomingMessage_UnknownType() throws Exception {
        String json = "{\"type\":\"unknown_type\",\"data\":{}}";

        java.lang.reflect.Method method = NetworkClient.class.getDeclaredMethod("handleIncomingMessage", String.class);
        method.setAccessible(true);
        assertDoesNotThrow(() -> method.invoke(networkClient, json));

        assertFalse(mockListener.gridUpdateReceived);
        assertFalse(mockListener.shotRequestReceived);
        assertFalse(mockListener.gameStatusReceived);
    }

    /**
     * Tests that multiple listeners receive the same message.
     */
    @Test
    void testHandleIncomingMessage_MultipleListeners() throws Exception {
        MockCommunicationEventsListener listener2 = new MockCommunicationEventsListener();
        networkClient.addCommunicationEventsListener(listener2);

        ShotRequestDTO shotRequest = new ShotRequestDTO(new Coordinate(5, 5));
        WebSocketMessage<ShotRequestDTO> message = new WebSocketMessage<>(
            GameMessageType.SHOT_REQUEST.getType(),
            shotRequest
        );
        String json = gson.toJson(message);

        java.lang.reflect.Method method = NetworkClient.class.getDeclaredMethod("handleIncomingMessage", String.class);
        method.setAccessible(true);
        method.invoke(networkClient, json);

        assertTrue(mockListener.shotRequestReceived);
        assertTrue(listener2.shotRequestReceived);
        assertEquals(mockListener.lastShotRequest, listener2.lastShotRequest);
    }

    /**
     * Tests that after removing a listener, it no longer receives messages.
     */
    @Test
    void testHandleIncomingMessage_RemovedListener() throws Exception {
        MockCommunicationEventsListener listener2 = new MockCommunicationEventsListener();
        networkClient.addCommunicationEventsListener(listener2);
        networkClient.removeCommunicationEventsListener(mockListener);

        ShotRequestDTO shotRequest = new ShotRequestDTO(new Coordinate(2, 2));
        WebSocketMessage<ShotRequestDTO> message = new WebSocketMessage<>(
            GameMessageType.SHOT_REQUEST.getType(),
            shotRequest
        );
        String json = gson.toJson(message);

        java.lang.reflect.Method method = NetworkClient.class.getDeclaredMethod("handleIncomingMessage", String.class);
        method.setAccessible(true);
        method.invoke(networkClient, json);

        assertFalse(mockListener.shotRequestReceived);
        assertTrue(listener2.shotRequestReceived);
    }

    /**
     * Tests handling of malformed JSON messages.
     */
    @Test
    void testHandleIncomingMessage_MalformedJson() throws Exception {
        String malformedJson = "{invalid json";

        java.lang.reflect.Method method = NetworkClient.class.getDeclaredMethod("handleIncomingMessage", String.class);
        method.setAccessible(true);
        assertDoesNotThrow(() -> method.invoke(networkClient, malformedJson));
    }

    /**
     * Mock implementation of CommunicationEvents for testing.
     */
    private static class MockCommunicationEventsListener implements CommunicationEvents {
        boolean gridUpdateReceived = false;
        boolean shotRequestReceived = false;
        boolean gameStatusReceived = false;

        GridUpdateDTO lastGridUpdate = null;
        ShotRequestDTO lastShotRequest = null;
        GameStatusDTO lastGameStatus = null;

        @Override
        public void onOpponentGridUpdate(GridUpdateDTO gridUpdateDTO) {
            this.gridUpdateReceived = true;
            this.lastGridUpdate = gridUpdateDTO;
        }

        @Override
        public void onShotReceived(ShotRequestDTO shotRequestDTO) {
            this.shotRequestReceived = true;
            this.lastShotRequest = shotRequestDTO;
        }

        @Override
        public void onGameStatusReceived(GameStatusDTO gameStatusDTO) {
            this.gameStatusReceived = true;
            this.lastGameStatus = gameStatusDTO;
        }
    }
}
