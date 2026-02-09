package battleship.controller.turn;

import battleship.controller.mode.GameModeStrategy;
import battleship.controller.turn.states.ActiveTurnState;
import battleship.controller.turn.states.GameOverState;
import battleship.controller.turn.states.SetupState;
import battleship.model.*;
import battleship.view.core.BattleshipView;
import it.units.battleship.CellState;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link TurnManager}.
 * Not super fancy tests, but they cover most of the state-related behavior.
 * If something breaks here, chances are the state machine logic changed.
 */
class TestTurnManager {

    @Mock
    private Grid grid;                    // keeping names short, they're obvious here
    @Mock
    private FleetManager fleetManager;
    @Mock
    private BattleshipView view;
    @Mock
    private GameModeStrategy gameMode;    // slightly different naming style, on purpose

    private TurnManager manager;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks is old-school but still clear enough
        MockitoAnnotations.openMocks(this);

        // Mock FleetManager and Grid methods needed by BaseGameState.onEnter
        when(fleetManager.getGrid()).thenReturn(grid);
        CellState[][] emptyGrid = new CellState[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                emptyGrid[i][j] = CellState.EMPTY;
            }
        }
        when(grid.getGrid()).thenReturn(emptyGrid);
        // TurnManager constructor creates opponentGrid using grid.getRow() and grid.getCol()
        when(grid.getRow()).thenReturn(10);
        when(grid.getCol()).thenReturn(10);
        when(fleetManager.getFleet()).thenReturn(java.util.List.of());
        when(fleetManager.getPlacedCounts()).thenReturn(java.util.Map.of());
        when(fleetManager.getRequiredFleetConfiguration()).thenReturn(java.util.Map.of());

        manager = new TurnManager(grid, fleetManager, view, gameMode);
    }

    @Test
    void constructor_startsInSetupState() {
        // sanity check: manager should always start in setup
        assertNotNull(manager.getCurrentState());
        assertTrue(manager.getCurrentState() instanceof SetupState);
        assertEquals(GameState.SETUP.name(), manager.getCurrentStateName());
    }

    @Test
    void start_setsPlayerTurnOnView() {
        manager.start();
        verify(view).setPlayerTurn(true);
    }

    @Test
    void transitionTo_replacesCurrentState() {
        TurnState active = new ActiveTurnState();

        manager.transitionTo(active);

        assertEquals(active, manager.getCurrentState());
        assertEquals(GameState.ACTIVE_TURN.name(), manager.getCurrentStateName());
    }

    @Test
    void transitionTo_callsOnEnterOnNewState() {
        TurnState fakeState = mock(TurnState.class);
        when(fakeState.getStateName()).thenReturn("MOCK_STATE"); // state name doesn't really matter here

        manager.transitionTo(fakeState);

        verify(fakeState).onEnter(manager);
    }

    @Test
    void canShoot_isFalseDuringSetup() {
        assertFalse(manager.canShoot());
    }

    @Test
    void canShoot_isTrueDuringActiveTurn() {
        manager.transitionTo(new ActiveTurnState());
        assertTrue(manager.canShoot());
    }

    @Test
    void canPlaceShip_allowedInSetup() {
        assertTrue(manager.canPlaceShip());
    }

    @Test
    void canPlaceShip_notAllowedInActiveTurn() {
        manager.transitionTo(new ActiveTurnState());
        assertFalse(manager.canPlaceShip());
    }

    @Test
    void playerGridClick_isDelegatedToState() {
        TurnState state = mock(TurnState.class);
        manager.transitionTo(state);

        Coordinate c = new Coordinate(0, 0);
        manager.handlePlayerGridClick(c);

        verify(state).handlePlayerGridClick(manager, c);
    }

    @Test
    void opponentGridClick_isDelegatedToState() {
        TurnState state = mock(TurnState.class);
        manager.transitionTo(state);

        Coordinate c = new Coordinate(0, 0);
        manager.handleOpponentGridClick(c);

        verify(state).handleOpponentGridClick(manager, c);
    }

    @Test
    void playerGridHover_isDelegatedToState() {
        TurnState state = mock(TurnState.class);
        manager.transitionTo(state);

        Coordinate c = new Coordinate(0, 0);
        manager.handlePlayerGridHover(c);

        verify(state).handlePlayerGridHover(manager, c);
    }

    @Test
    void opponentGridHover_isDelegatedToState() {
        TurnState state = mock(TurnState.class);
        manager.transitionTo(state);

        Coordinate c = new Coordinate(0, 0);
        manager.handleOpponentGridHover(c);

        verify(state).handleOpponentGridHover(manager, c);
    }

    @Test
    void incomingShot_isHandledByState() {
        TurnState state = mock(TurnState.class);
        manager.transitionTo(state);

        Coordinate c = new Coordinate(0, 0);
        manager.handleIncomingShot(c);

        verify(state).handleIncomingShot(manager, c);
    }

    @Test
    void opponentGridUpdate_isForwardedToState() {
        TurnState state = mock(TurnState.class);
        manager.transitionTo(state);

        String serializedGrid = "grid_data"; // doesn't need to be realistic
        List<Ship> ships = List.of();

        manager.handleOpponentGridUpdate(serializedGrid, ships);

        verify(state).handleOpponentGridUpdate(manager, serializedGrid, ships);
    }

    @Test
    void gameOver_forcesGameOverState() {
        manager.handleGameOver("You lost!"); // message currently ignored internally

        assertTrue(manager.getCurrentState() instanceof GameOverState);
        assertEquals(GameState.GAME_OVER.name(), manager.getCurrentStateName());
    }

    @Test
    void gameStatusReceived_isDelegatedToState() {
        TurnState state = mock(TurnState.class);
        manager.transitionTo(state);

        manager.handleGameStatusReceived(GameState.ACTIVE_TURN);

        verify(state).handleGameStatusReceived(manager, GameState.ACTIVE_TURN);
    }

    @Test
    void setupComplete_callsCallbackIfPresent() {
        TurnManager.SetupCompleteCallback callback =
                mock(TurnManager.SetupCompleteCallback.class);

        manager.setSetupCompleteCallback(callback);
        manager.onSetupComplete();

        verify(callback).onSetupComplete();
    }

    @Test
    void setupComplete_withoutCallback_doesNotCrash() {
        // defensive test: nothing should explode here
        assertDoesNotThrow(() -> manager.onSetupComplete());
    }

    @Test
    void requestShot_mapsToOpponentGridClick() {
        TurnState state = mock(TurnState.class);
        manager.transitionTo(state);

        Coordinate c = new Coordinate(0, 0);
        manager.requestShot(c);

        verify(state).handleOpponentGridClick(manager, c);
    }

    @Test
    void requestShipPlacement_mapsToPlayerGridClick() {
        TurnState state = mock(TurnState.class);
        manager.transitionTo(state);

        Coordinate c = new Coordinate(0, 0);
        manager.requestShipPlacement(c);

        verify(state).handlePlayerGridClick(manager, c);
    }

    @Test
    void requestPlacementPreview_mapsToPlayerHover() {
        TurnState state = mock(TurnState.class);
        manager.transitionTo(state);

        Coordinate c = new Coordinate(0, 0);
        manager.requestPlacementPreview(c);

        verify(state).handlePlayerGridHover(manager, c);
    }

    @Test
    void previewShot_mapsToOpponentHover() {
        TurnState state = mock(TurnState.class);
        manager.transitionTo(state);

        Coordinate c = new Coordinate(0, 0);
        manager.previewShot(c);

        verify(state).handleOpponentGridHover(manager, c);
    }

    @Test
    void getters_returnInjectedDependencies() {
        // basic getters test – boring but useful when refactoring
        assertEquals(grid, manager.getGrid());
        assertEquals(fleetManager, manager.getFleetManager());
        assertEquals(view, manager.getView());
        assertEquals(gameMode, manager.getGameModeStrategy());
    }
}
