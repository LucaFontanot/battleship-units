package battleship.controller;

import battleship.controller.actions.NetworkOutputActions;
import battleship.controller.handlers.network.NetworkInputHandler;
import battleship.controller.network.AbstractPlayerCommunication;
import battleship.model.FleetManager;
import battleship.model.Grid;
import battleship.model.Ship;
import battleship.view.GameView;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.data.socket.GameMessageType;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestGameController {

    @Mock
    private GameView mockView;

    @Mock
    private Grid mockGrid;

    @Mock
    private FleetManager mockFleetManager;

    @Mock
    private AbstractPlayerCommunication mockCommunication;

    private GameController gameController;

    private NetworkInputHandler networkInputHandler;

    private NetworkOutputActions networkOutputHandler;

    @BeforeEach
    void setup() {
        //networkOutputHandler = new NetworkOutputHandler(mockCommunication);
        //gameController = new GameController(mockGrid, mockFleetManager, networkOutputHandler, mockView);
        networkInputHandler = new NetworkInputHandler(gameController);
    }

    @Test
    void testInitialization() {
        assertNotNull(gameController, "Game controller was not initialized correctly.");
        assertEquals(GameState.WAITING_FOR_SETUP, gameController.getGameState());
    }

    @Test
    void testOnOpponentGridUpdate() {
        GridUpdateDTO dto = new GridUpdateDTO(false, "0".repeat(100), List.of());
        networkInputHandler.onOpponentGridUpdate(dto);
        verify(mockView).updateOpponentGrid(anyString(), anyList());
    }

    @Test
    void testShotReceivedProcessesHitAndSendsResponse() {
        // Arrange
        Coordinate coord = new Coordinate(1, 1);
        ShotRequestDTO shotRequest = new ShotRequestDTO(coord);
        
        when(mockFleetManager.handleIncomingShot(coord)).thenReturn(true);
        it.units.battleship.CellState[][] gridArray = new it.units.battleship.CellState[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                gridArray[i][j] = it.units.battleship.CellState.EMPTY;
            }
        }
        when(mockGrid.getGrid()).thenReturn(gridArray);
        when(mockFleetManager.getFleet()).thenReturn(List.of());

        // Act
        networkInputHandler.onShotReceived(shotRequest);

        // Assert
        verify(mockFleetManager).handleIncomingShot(coord);

        ArgumentCaptor<GridUpdateDTO> captor = ArgumentCaptor.forClass(GridUpdateDTO.class);
        verify(mockCommunication).sendMessage(eq(GameMessageType.GRID_UPDATE), captor.capture());
        assertTrue(captor.getValue().shotOutcome());

        verify(mockView).updatePlayerGrid(eq("0".repeat(100)), anyList());
    }

    @Test
    void testShotReceivedOnlyRevealsSunkShips() {
        // Arrange
        Coordinate coord = new Coordinate(0, 0);
        ShotRequestDTO shotRequest = new ShotRequestDTO(coord);
        
        Ship aliveShip = mock(Ship.class);
        when(aliveShip.isSunk()).thenReturn(false);
        
        Ship sunkShip = mock(Ship.class);
        when(sunkShip.isSunk()).thenReturn(true);
        
        when(mockFleetManager.getFleet()).thenReturn(List.of(aliveShip, sunkShip));
        it.units.battleship.CellState[][] gridArray = new it.units.battleship.CellState[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                gridArray[i][j] = it.units.battleship.CellState.EMPTY;
            }
        }
        when(mockGrid.getGrid()).thenReturn(gridArray);

        // Act
        networkInputHandler.onShotReceived(shotRequest);

        // Assert
        ArgumentCaptor<GridUpdateDTO> captor = ArgumentCaptor.forClass(GridUpdateDTO.class);
        verify(mockCommunication).sendMessage(eq(GameMessageType.GRID_UPDATE), captor.capture());
        assertEquals(1, captor.getValue().fleet().size());
    }

    @Test
    void testProcessGameStatusUpdate() {
        it.units.battleship.data.socket.payloads.GameStatusDTO statusDTO = new it.units.battleship.data.socket.payloads.GameStatusDTO(it.units.battleship.GameState.GAME_OVER, "Game Over");
        networkInputHandler.onGameStatusReceived(statusDTO);
        
        assertEquals(it.units.battleship.GameState.GAME_OVER, gameController.getGameState());
        verify(mockView).showEndGamePhase("Game Over");
    }
}