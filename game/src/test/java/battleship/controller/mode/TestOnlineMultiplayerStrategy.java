package battleship.controller.mode;

import it.units.battleship.controller.game.events.CommunicationEvents;
import it.units.battleship.controller.game.network.NetworkClient;
import it.units.battleship.controller.mode.GameModeStrategy;
import it.units.battleship.controller.mode.OnlineMultiplayerStrategy;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.data.socket.payloads.GameStatusDTO;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestOnlineMultiplayerStrategy {

    private OnlineMultiplayerStrategy strategy;
    private NetworkClient mockNetworkClient;
    private GameModeStrategy.GameModeCallback mockCallback;

    @BeforeEach
    void setUp() {
        mockNetworkClient = mock(NetworkClient.class);
        mockCallback = mock(GameModeStrategy.GameModeCallback.class);
        strategy = new OnlineMultiplayerStrategy(mockNetworkClient);
    }

    @Test
    void testGetModeNameReturnsOnlineMultiplayer() {
        assertEquals("Online Multiplayer", strategy.getModeName());
    }

    @Test
    void testInitializeAddsListenerToNetworkClient() {
        strategy.initialize(mockCallback);

        verify(mockNetworkClient).addCommunicationEventsListener(any(CommunicationEvents.class));
    }

    @Test
    void testSendShotDelegatesToNetworkClient() {
        Coordinate coord = new Coordinate(3, 5);

        strategy.sendShot(coord);

        verify(mockNetworkClient).sendShotRequest(coord);
    }

    @Test
    void testSendGridUpdateDelegatesToNetworkClient() {
        Grid mockGrid = mock(Grid.class);
        List<Ship> fleet = List.of();

        strategy.sendGridUpdate(mockGrid, fleet, true);

        verify(mockNetworkClient).sendGridUpdate(mockGrid, fleet, true);
    }

    @Test
    void testSendGameOverDelegatesToNetworkClient() {
        strategy.sendGameOver("Game over!");

        verify(mockNetworkClient).sendGameStatus(GameState.GAME_OVER, "Game over!");
    }

    @Test
    void testNotifySetupCompleteSendsWaitingSetupStatus() {
        strategy.notifySetupComplete();

        verify(mockNetworkClient).sendGameStatus(GameState.WAITING_SETUP, "Ready to play");
    }

    @Test
    void testShutdownDoesNotThrow() {
        assertDoesNotThrow(() -> strategy.shutdown());
    }

    @Test
    void testIncomingShotIsRoutedToCallback() {
        ArgumentCaptor<CommunicationEvents> captor = ArgumentCaptor.forClass(CommunicationEvents.class);
        strategy.initialize(mockCallback);
        verify(mockNetworkClient).addCommunicationEventsListener(captor.capture());

        CommunicationEvents listener = captor.getValue();
        Coordinate expectedCoord = new Coordinate(2, 3);
        listener.onShotReceived(new ShotRequestDTO(expectedCoord));

        verify(mockCallback).onShotReceived(expectedCoord);
    }

    @Test
    void testIncomingGameStatusIsRoutedToCallback() {
        ArgumentCaptor<CommunicationEvents> captor = ArgumentCaptor.forClass(CommunicationEvents.class);
        strategy.initialize(mockCallback);
        verify(mockNetworkClient).addCommunicationEventsListener(captor.capture());

        CommunicationEvents listener = captor.getValue();
        listener.onGameStatusReceived(new GameStatusDTO(GameState.ACTIVE_TURN, "Your turn"));

        verify(mockCallback).onGameStatusReceived(GameState.ACTIVE_TURN, "Your turn");
    }

    @Test
    void testIncomingGridUpdateIsRoutedToCallback() {
        ArgumentCaptor<CommunicationEvents> captor = ArgumentCaptor.forClass(CommunicationEvents.class);
        strategy.initialize(mockCallback);
        verify(mockNetworkClient).addCommunicationEventsListener(captor.capture());

        CommunicationEvents listener = captor.getValue();
        String gridSerialized = "EEEEEEEEEE";
        listener.onOpponentGridUpdate(new GridUpdateDTO(true, gridSerialized, List.of()));

        verify(mockCallback).onGridUpdateReceived(eq(gridSerialized), anyList());
    }

    @Test
    void testIncomingGameOverStatusIsRoutedToCallback() {
        ArgumentCaptor<CommunicationEvents> captor = ArgumentCaptor.forClass(CommunicationEvents.class);
        strategy.initialize(mockCallback);
        verify(mockNetworkClient).addCommunicationEventsListener(captor.capture());

        CommunicationEvents listener = captor.getValue();
        listener.onGameStatusReceived(new GameStatusDTO(GameState.GAME_OVER, "You lost!"));

        verify(mockCallback).onGameStatusReceived(GameState.GAME_OVER, "You lost!");
    }
}
