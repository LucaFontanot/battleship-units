package battleship.controller.turn.states;

import battleship.controller.mode.GameModeStrategy;
import battleship.controller.turn.TurnManager;
import battleship.model.*;
import battleship.view.core.BattleshipView;
import it.units.battleship.CellState;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestSetupState {

    @Mock
    private TurnManager mockTurnManager;

    private SetupState setupState;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        setupState = new SetupState();
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
        setupState.onEnter(mockTurnManager);

        verify(mockTurnManager).setPlayerTurn(true);
        verify(mockTurnManager).refreshFleetUI();
        verify(mockTurnManager).refreshUI();
    }

    @Test
    void testHandlePlayerGridClickDelegatesToManager() {
        Coordinate coord = new Coordinate(0, 0);

        setupState.handlePlayerGridClick(mockTurnManager, coord);

        verify(mockTurnManager).tryPlaceShip(coord);
    }

    @Test
    void testHandlePlayerGridHoverDelegatesToManager() {
        Coordinate coord = new Coordinate(0, 0);

        setupState.handlePlayerGridHover(mockTurnManager, coord);

        verify(mockTurnManager).previewPlacement(coord);
    }
}
