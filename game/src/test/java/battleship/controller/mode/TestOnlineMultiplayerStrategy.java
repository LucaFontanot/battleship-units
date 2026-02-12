package battleship.controller.mode;

import battleship.testutil.FakeCallback;
import battleship.testutil.FakeNetworkClient;
import it.units.battleship.controller.mode.OnlineMultiplayerStrategy;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.data.socket.GameMessageType;
import it.units.battleship.data.socket.payloads.GameStatusDTO;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestOnlineMultiplayerStrategy {

    private OnlineMultiplayerStrategy strategy;
    private FakeNetworkClient fakeNetwork;
    private FakeCallback fakeCallback;

    @BeforeEach
    void setUp() {
        fakeNetwork = new FakeNetworkClient();
        fakeCallback = new FakeCallback();
        strategy = new OnlineMultiplayerStrategy(fakeNetwork);
    }

    @Test
    void testGetModeNameReturnsOnlineMultiplayer() {
        assertEquals("Online Multiplayer", strategy.getModeName());
    }

    @Test
    void testInitializeRegistersListenerOnNetwork() {
        strategy.initialize(fakeCallback);

        // After initialize, a listener should been registered on the network
        // We can verify by sending an event and checking the callback receives it
        fakeNetwork.onShotReceived(new ShotRequestDTO(new Coordinate(0, 0)));
        assertNotNull(fakeCallback.lastShotReceived);
    }

    @Test
    void testSendShotDelegatesToNetwork() {
        Coordinate coord = new Coordinate(3, 5);

        strategy.sendShot(coord);

        assertEquals(GameMessageType.SHOT_REQUEST, fakeNetwork.lastMessageType);
        assertInstanceOf(ShotRequestDTO.class, fakeNetwork.lastMessagePayload);
    }

    @Test
    void testSendGridUpdateDelegatesToNetwork() {
        Grid grid = new Grid(10, 10);
        List<Ship> fleet = List.of();

        strategy.sendGridUpdate(grid, fleet, true);

        assertEquals(GameMessageType.GRID_UPDATE, fakeNetwork.lastMessageType);
        assertInstanceOf(GridUpdateDTO.class, fakeNetwork.lastMessagePayload);
    }

    @Test
    void testSendGameOverDelegatesToNetwork() {
        strategy.sendGameOver("Game over!");

        assertEquals(GameMessageType.TURN_CHANGE, fakeNetwork.lastMessageType);
        assertInstanceOf(GameStatusDTO.class, fakeNetwork.lastMessagePayload);
        GameStatusDTO payload = (GameStatusDTO) fakeNetwork.lastMessagePayload;
        assertEquals(GameState.GAME_OVER, payload.state());
    }

    @Test
    void testNotifySetupCompleteSendsWaitingSetupStatus() {
        strategy.notifySetupComplete();

        assertEquals(GameMessageType.TURN_CHANGE, fakeNetwork.lastMessageType);
        assertInstanceOf(GameStatusDTO.class, fakeNetwork.lastMessagePayload);
        GameStatusDTO payload = (GameStatusDTO) fakeNetwork.lastMessagePayload;
        assertEquals(GameState.WAITING_SETUP, payload.state());
    }

    @Test
    void testShutdownDoesNotThrow() {
        assertDoesNotThrow(() -> strategy.shutdown());
    }

    @Test
    void testIncomingShotIsRoutedToCallback() {
        strategy.initialize(fakeCallback);

        Coordinate expectedCoord = new Coordinate(2, 3);
        fakeNetwork.onShotReceived(new ShotRequestDTO(expectedCoord));

        assertEquals(expectedCoord, fakeCallback.lastShotReceived);
    }

    @Test
    void testIncomingGameStatusIsRoutedToCallback() {
        strategy.initialize(fakeCallback);

        fakeNetwork.onGameStatusReceived(new GameStatusDTO(GameState.ACTIVE_TURN, "Your turn"));

        assertEquals(GameState.ACTIVE_TURN, fakeCallback.lastGameStatusState);
        assertEquals("Your turn", fakeCallback.lastGameStatusMessage);
    }

    @Test
    void testIncomingGridUpdateIsRoutedToCallback() {
        strategy.initialize(fakeCallback);

        String gridSerialized = "EEEEEEEEEE";
        fakeNetwork.onOpponentGridUpdate(new GridUpdateDTO(true, gridSerialized, List.of()));

        assertEquals(gridSerialized, fakeCallback.lastGridUpdateSerialized);
        assertNotNull(fakeCallback.lastGridUpdateFleet);
    }

    @Test
    void testIncomingGameOverStatusIsRoutedToCallback() {
        strategy.initialize(fakeCallback);

        fakeNetwork.onGameStatusReceived(new GameStatusDTO(GameState.GAME_OVER, "You lost!"));

        assertEquals(GameState.GAME_OVER, fakeCallback.lastGameStatusState);
        assertEquals("You lost!", fakeCallback.lastGameStatusMessage);
    }
}
