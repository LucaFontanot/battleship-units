import battleship.controller.GameController;
import battleship.model.FleetManager;
import battleship.model.Grid;
import battleship.model.Orientation;
import battleship.model.Ship;
import battleship.model.ShipType;
import battleship.view.GameView;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestGameController {

    @Mock
    private GameView mockView;

    @Mock
    private Grid mockGrid;

    @Mock
    private FleetManager mockFleetManager;

    private GameController gameController;

    @BeforeEach
    void setup(){
        org.mockito.Mockito.lenient().when(mockGrid.getRow()).thenReturn(10);
        org.mockito.Mockito.lenient().when(mockGrid.getCol()).thenReturn(10);
        gameController = new GameController(mockGrid, mockFleetManager, mockView);
    }

    @Test
    void testInitialization(){
        assertNotNull(gameController, "Game controller was not initialized correctly.");
    }

    @Test
    void testStartGame(){
        when(mockGrid.gridSerialization()).thenReturn("");
        when(mockFleetManager.getFleet()).thenReturn(java.util.Collections.emptyList());

        gameController.startGame();
        verify(mockView).showSetupPhase();
        verify(mockView).updateSystemMessage(anyString());
        verify(mockView).updatePlayerGrid(anyString(), anyList());
        assertEquals(GameState.SETUP, gameController.getGameState());
    }

    @Test
    void testPlaceShipSuccess() {
        // Setup interactions
        when(mockFleetManager.addShip(any(Ship.class))).thenReturn(true);
        when(mockGrid.gridSerialization()).thenReturn("grid_state");
        when(mockFleetManager.getFleet()).thenReturn(java.util.Collections.emptyList());

        // Action
        gameController.placeShip(ShipType.DESTROYER, Orientation.HORIZONTAL_RIGHT, new Coordinate(0, 0));

        // Verify
        verify(mockView).updateSystemMessage("Ship placed successfully.");
        verify(mockView).updatePlayerGrid(anyString(), anyList());
        verify(mockView, never()).displayErrorAlert(anyString());
    }

    @Test
    void testPlaceShipFailure() {
        // Setup interactions: FleetManager returns false (e.g., collision)
        when(mockFleetManager.addShip(any(Ship.class))).thenReturn(false);

        // Action
        gameController.placeShip(ShipType.DESTROYER, Orientation.HORIZONTAL_RIGHT, new Coordinate(0, 0));

        // Verify
        verify(mockView).displayErrorAlert(anyString());
        verify(mockView, never()).updateSystemMessage("Ship placed successfully.");
    }

    @Test
    void testRemoveShipSuccess() {
        // Setup interactions
        when(mockFleetManager.removeShipByCoordinate(any(Coordinate.class))).thenReturn(true);
        when(mockGrid.gridSerialization()).thenReturn("grid_state");
        when(mockFleetManager.getFleet()).thenReturn(java.util.Collections.emptyList());

        // Action
        gameController.removeShip(new Coordinate(0, 0));

        // Verify
        verify(mockView).updateSystemMessage("Ship removed successfully.");
        verify(mockView).updatePlayerGrid(anyString(), anyList());
    }

    @Test
    void testRemoveShipFailure() {
        // Setup interactions
        when(mockFleetManager.removeShipByCoordinate(any(Coordinate.class))).thenReturn(false);

        // Action
        gameController.removeShip(new Coordinate(0, 0));

        // Verify
        verify(mockView).displayErrorAlert("Ship removal failed. Please try again.");
    }

    @Test
    void testConfirmSetupSuccess() {
        // Setup interactions
        when(mockFleetManager.isFleetComplete()).thenReturn(true);

        // Action
        gameController.confirmSetup();

        // Verify
        assertEquals(GameState.WAITING, gameController.getGameState());
        verify(mockView).showGamePhase();
    }

    @Test
    void testConfirmSetupFailure() {
        // Setup interactions
        when(mockFleetManager.isFleetComplete()).thenReturn(false);

        // Action
        gameController.confirmSetup();

        // Verify
        verify(mockView).displayErrorAlert(anyString());
        assertEquals(GameState.SETUP, gameController.getGameState());
    }
}
