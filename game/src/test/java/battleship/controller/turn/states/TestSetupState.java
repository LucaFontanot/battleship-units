package battleship.controller.turn.states;

import battleship.controller.mode.GameModeStrategy;
import battleship.controller.turn.TurnManager;
import battleship.model.FleetManager;
import battleship.model.Grid;
import battleship.model.Ship;
import battleship.view.GameView;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedHashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

class TestSetupState {

    @Mock
    private TurnManager mockTurnManager;
    @Mock
    private GameView mockView;
    @Mock
    private FleetManager mockFleetManager;
    @Mock
    private Grid mockGrid;
    @Mock
    private GameModeStrategy mockGameModeStrategy;

    private SetupState setupState;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setupState = new SetupState();

        when(mockTurnManager.getView()).thenReturn(mockView);
        when(mockTurnManager.getFleetManager()).thenReturn(mockFleetManager);
        when(mockTurnManager.getGrid()).thenReturn(mockGrid);
        when(mockTurnManager.getGameModeStrategy()).thenReturn(mockGameModeStrategy);
    }

    @Test
    void testGetStateNameReturnsSetup() {
        assertEquals(GameState.SETUP.name(), setupState.getStateName());
    }

    @Test
    void testCanPlaceShipReturnsTrue() {
        assertTrue(setupState.canPlaceShip());
    }

    @Test
    void testCanShootReturnsFalse() {
        assertFalse(setupState.canShoot());
    }

    @Test
    void testOnEnterSetsPlayerTurnTrue() {
        when(mockFleetManager.getPlacedCounts()).thenReturn(Map.of());
        when(mockFleetManager.getRequiredFleetConfiguration()).thenReturn(Map.of());

        setupState.onEnter(mockTurnManager);

        verify(mockView).setPlayerTurn(true);
        verify(mockView).refreshFleetSelection(any(), any());
    }

    @Test
    void testHandlePlayerGridClickWithNullShipTypeDoesNothing() {
        when(mockView.getSelectedShipType()).thenReturn(null);

        setupState.handlePlayerGridClick(mockTurnManager, new Coordinate(0, 0));

        verify(mockFleetManager, never()).addShip(any());
    }

    @Test
    void testHandlePlayerGridClickPlacesShipSuccessfully() {
        Coordinate coord = new Coordinate(0, 0);
        ShipType shipType = ShipType.BATTLESHIP;
        Orientation orientation = Orientation.HORIZONTAL_RIGHT;

        when(mockView.getSelectedShipType()).thenReturn(shipType);
        when(mockView.getSelectedOrientation()).thenReturn(orientation);
        when(mockFleetManager.addShip(any(Ship.class))).thenReturn(true);
        when(mockFleetManager.isFleetComplete()).thenReturn(false);
        when(mockFleetManager.getGrid()).thenReturn(mockGrid);
        when(mockGrid.getRow()).thenReturn(10);
        when(mockGrid.getCol()).thenReturn(10);
        when(mockFleetManager.getPlacedCounts()).thenReturn(Map.of());
        when(mockFleetManager.getRequiredFleetConfiguration()).thenReturn(Map.of());

        setupState.handlePlayerGridClick(mockTurnManager, coord);

        verify(mockFleetManager).addShip(any(Ship.class));
        verify(mockView).updatePlayerGrid(any(), any());
        verify(mockView).refreshFleetSelection(any(), any());
    }

    @Test
    void testHandlePlayerGridClickCallsOnSetupCompleteWhenFleetComplete() {
        Coordinate coord = new Coordinate(0, 0);
        ShipType shipType = ShipType.BATTLESHIP;
        Orientation orientation = Orientation.HORIZONTAL_RIGHT;

        when(mockView.getSelectedShipType()).thenReturn(shipType);
        when(mockView.getSelectedOrientation()).thenReturn(orientation);
        when(mockFleetManager.addShip(any(Ship.class))).thenReturn(true);
        when(mockFleetManager.isFleetComplete()).thenReturn(true);
        when(mockFleetManager.getGrid()).thenReturn(mockGrid);
        when(mockGrid.getRow()).thenReturn(10);
        when(mockGrid.getCol()).thenReturn(10);
        when(mockFleetManager.getPlacedCounts()).thenReturn(Map.of());
        when(mockFleetManager.getRequiredFleetConfiguration()).thenReturn(Map.of());

        setupState.handlePlayerGridClick(mockTurnManager, coord);

        verify(mockTurnManager).onSetupComplete();
    }

    @Test
    void testHandlePlayerGridClickPlaysErrorSoundOnInvalidPlacement() {
        Coordinate coord = new Coordinate(0, 0);
        ShipType shipType = ShipType.BATTLESHIP;
        Orientation orientation = Orientation.HORIZONTAL_RIGHT;

        when(mockView.getSelectedShipType()).thenReturn(shipType);
        when(mockView.getSelectedOrientation()).thenReturn(orientation);
        when(mockFleetManager.addShip(any(Ship.class))).thenReturn(false);
        when(mockFleetManager.getGrid()).thenReturn(mockGrid);

        setupState.handlePlayerGridClick(mockTurnManager, coord);

        verify(mockView).playerErrorSound();
        verify(mockView).showPlacementPreview(any(), eq(false), any());
    }

    @Test
    void testHandlePlayerGridClickHandlesIllegalArgumentException() {
        Coordinate coord = new Coordinate(0, 0);
        ShipType shipType = ShipType.BATTLESHIP;
        Orientation orientation = Orientation.HORIZONTAL_RIGHT;

        when(mockView.getSelectedShipType()).thenReturn(shipType);
        when(mockView.getSelectedOrientation()).thenReturn(orientation);
        when(mockFleetManager.getGrid()).thenThrow(new IllegalArgumentException("Invalid placement"));

        setupState.handlePlayerGridClick(mockTurnManager, coord);

        verify(mockView).playerErrorSound();
        verify(mockView).showPlacementPreview(any(LinkedHashSet.class), eq(false), isNull());
    }

    @Test
    void testHandlePlayerGridHoverShowsValidPreview() {
        Coordinate coord = new Coordinate(0, 0);
        ShipType shipType = ShipType.BATTLESHIP;
        Orientation orientation = Orientation.HORIZONTAL_RIGHT;

        when(mockView.getSelectedShipType()).thenReturn(shipType);
        when(mockView.getSelectedOrientation()).thenReturn(orientation);
        when(mockFleetManager.canPlaceShip(any(Ship.class))).thenReturn(true);
        when(mockFleetManager.getGrid()).thenReturn(mockGrid);
        when(mockGrid.getRow()).thenReturn(10);
        when(mockGrid.getCol()).thenReturn(10);

        setupState.handlePlayerGridHover(mockTurnManager, coord);

        verify(mockView).showPlacementPreview(any(), eq(true), any(Ship.class));
    }

    @Test
    void testHandlePlayerGridHoverShowsInvalidPreview() {
        Coordinate coord = new Coordinate(0, 0);
        ShipType shipType = ShipType.BATTLESHIP;
        Orientation orientation = Orientation.HORIZONTAL_RIGHT;

        when(mockView.getSelectedShipType()).thenReturn(shipType);
        when(mockView.getSelectedOrientation()).thenReturn(orientation);
        when(mockFleetManager.canPlaceShip(any(Ship.class))).thenReturn(false);
        when(mockFleetManager.getGrid()).thenReturn(mockGrid);
        when(mockGrid.getRow()).thenReturn(10);
        when(mockGrid.getCol()).thenReturn(10);

        setupState.handlePlayerGridHover(mockTurnManager, coord);

        verify(mockView).showPlacementPreview(any(), eq(false), any(Ship.class));
    }

    @Test
    void testHandlePlayerGridHoverWithNullShipTypeDoesNothing() {
        when(mockView.getSelectedShipType()).thenReturn(null);

        setupState.handlePlayerGridHover(mockTurnManager, new Coordinate(0, 0));

        verify(mockView, never()).showPlacementPreview(any(), anyBoolean(), any());
    }

    @Test
    void testHandlePlayerGridHoverHandlesIllegalArgumentException() {
        Coordinate coord = new Coordinate(0, 0);
        ShipType shipType = ShipType.BATTLESHIP;
        Orientation orientation = Orientation.HORIZONTAL_RIGHT;

        when(mockView.getSelectedShipType()).thenReturn(shipType);
        when(mockView.getSelectedOrientation()).thenReturn(orientation);
        when(mockFleetManager.getGrid()).thenThrow(new IllegalArgumentException("Invalid coordinates"));

        setupState.handlePlayerGridHover(mockTurnManager, coord);

        verify(mockView).showPlacementPreview(any(LinkedHashSet.class), eq(false), isNull());
    }
}
