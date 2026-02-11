package battleship.controller.turn.states;

import it.units.battleship.controller.turn.GameActions;
import it.units.battleship.controller.turn.states.SetupState;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestSetupState {

    @Mock
    private GameActions mockActions;

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
        setupState.onEnter(mockActions);

        verify(mockActions).setPlayerTurn(true);
        verify(mockActions, atLeastOnce()).refreshFleetUI();
    }

    @Test
    void testHandlePlayerGridClickDelegatesToActions() {
        Coordinate coord = new Coordinate(0, 0);

        setupState.handlePlayerGridClick(mockActions, coord);

        verify(mockActions).placeShip(coord);
    }

    @Test
    void testHandlePlayerGridHoverDelegatesToActions() {
        Coordinate coord = new Coordinate(0, 0);

        setupState.handlePlayerGridHover(mockActions, coord);

        verify(mockActions).previewPlacement(coord);
    }
}
