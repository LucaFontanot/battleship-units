package battleship.controller.turn;

import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.controller.turn.TurnManager;
import it.units.battleship.controller.turn.GameActions;
import it.units.battleship.controller.turn.states.ActiveTurnState;
import it.units.battleship.controller.turn.states.GameOverState;
import it.units.battleship.controller.turn.states.SetupState;
import it.units.battleship.controller.turn.states.WaitingOpponentState;
import it.units.battleship.model.Ship;
import it.units.battleship.CellState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestTurnManager {

    @Mock
    private GameActions mockActions;

    private TurnManager manager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        manager = new TurnManager(mockActions);
    }

    @Test
    void constructor_startsInSetupState() {
        assertNotNull(manager.getCurrentState());
        assertInstanceOf(SetupState.class, manager.getCurrentState());
        assertEquals(GameState.SETUP.name(), manager.getCurrentStateName());
    }

    @Test
    void start_entersInitialState() {
        manager.start();

        // SetupState.onEnter calls setPlayerTurn(true) and refreshFleetUI()
        verify(mockActions).setPlayerTurn(true);
        verify(mockActions, atLeastOnce()).refreshFleetUI();
    }

    @Test
    void transitionToActiveTurn_replacesCurrentState() {
        manager.transitionToActiveTurn();

        assertInstanceOf(ActiveTurnState.class, manager.getCurrentState());
        assertEquals(GameState.ACTIVE_TURN.name(), manager.getCurrentStateName());
    }

    @Test
    void canShoot_isFalseDuringSetup() {
        assertFalse(manager.canShoot());
    }

    @Test
    void canShoot_isTrueDuringActiveTurn() {
        manager.transitionToActiveTurn();
        assertTrue(manager.canShoot());
    }

    @Test
    void canPlaceShip_allowedInSetup() {
        assertTrue(manager.canPlaceShip());
    }

    @Test
    void canPlaceShip_notAllowedInActiveTurn() {
        manager.transitionToActiveTurn();
        assertFalse(manager.canPlaceShip());
    }

    @Test
    void playerGridClick_isDelegatedToCurrentState() {
        // In SetupState, playerGridClick calls actions.placeShip()
        Coordinate c = new Coordinate(0, 0);
        manager.handlePlayerGridClick(c);

        verify(mockActions).placeShip(c);
    }

    @Test
    void opponentGridClick_isDelegatedToCurrentState() {
        // Transition to ActiveTurnState so opponentGridClick has effect
        manager.transitionToActiveTurn();

        Coordinate c = new Coordinate(0, 0);
        when(mockActions.getOpponentCellState(c)).thenReturn(CellState.EMPTY);

        manager.handleOpponentGridClick(c);

        verify(mockActions).fireShot(c);
    }

    @Test
    void playerGridHover_isDelegatedToCurrentState() {
        // In SetupState, playerGridHover calls actions.previewPlacement()
        Coordinate c = new Coordinate(0, 0);
        manager.handlePlayerGridHover(c);

        verify(mockActions).previewPlacement(c);
    }

    @Test
    void opponentGridHover_isDelegatedToCurrentState() {
        // Transition to ActiveTurnState so opponentGridHover has effect
        manager.transitionToActiveTurn();

        Coordinate c = new Coordinate(3, 5);
        manager.handleOpponentGridHover(c);

        verify(mockActions).showShotPreview(c);
    }

    @Test
    void incomingShot_isDelegatedToCurrentState() {
        // Transition to WaitingOpponentState where incoming shots are handled
        manager.transitionToWaitingOpponent();

        Coordinate c = new Coordinate(0, 0);
        when(mockActions.processIncomingShot(c)).thenReturn(false);

        manager.handleIncomingShot(c);

        verify(mockActions).processIncomingShot(c);
        verify(mockActions).transitionToActiveTurn();
    }

    @Test
    void opponentGridUpdate_isForwardedToCurrentState() {
        String serializedGrid = "grid_data";
        List<Ship> ships = List.of();

        manager.handleOpponentGridUpdate(serializedGrid, ships);

        // BaseGameState.handleOpponentGridUpdate calls actions.updateOpponentGrid()
        verify(mockActions).updateOpponentGrid(serializedGrid, ships);
    }

    @Test
    void gameOver_forcesGameOverState() {
        manager.handleGameOver("You lost!");

        assertInstanceOf(GameOverState.class, manager.getCurrentState());
        assertEquals(GameState.GAME_OVER.name(), manager.getCurrentStateName());
    }

    @Test
    void gameStatusReceived_isDelegatedToCurrentState() {
        // Transition to WaitingSetupState to handle game status
        manager.transitionToWaitingSetup();

        manager.handleGameStatusReceived(GameState.ACTIVE_TURN);

        verify(mockActions).transitionToGamePhase();
        verify(mockActions).transitionToActiveTurn();
    }

    @Test
    void getActions_returnsInjectedActions() {
        assertEquals(mockActions, manager.getActions());
    }
}
