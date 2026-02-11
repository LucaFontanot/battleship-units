package battleship.controller.turn;

import it.units.battleship.controller.mode.GameModeStrategy;
import it.units.battleship.controller.turn.TurnManager;
import it.units.battleship.controller.turn.TurnState;
import it.units.battleship.controller.turn.states.ActiveTurnState;
import it.units.battleship.controller.turn.states.GameOverState;
import it.units.battleship.controller.turn.states.SetupState;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Ship;
import it.units.battleship.view.core.BattleshipView;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestTurnManager {

    @Mock
    private FleetManager fleetManager;
    @Mock
    private BattleshipView view;
    @Mock
    private GameModeStrategy gameMode;

    private TurnManager manager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock FleetManager methods needed by TurnManager constructor and onEnter
        when(fleetManager.getGridRows()).thenReturn(10);
        when(fleetManager.getGridCols()).thenReturn(10);
        when(fleetManager.getFleet()).thenReturn(java.util.List.of());
        when(fleetManager.getPlacedCounts()).thenReturn(java.util.Map.of());
        when(fleetManager.getRequiredFleetConfiguration()).thenReturn(java.util.Map.of());
        when(fleetManager.getSerializedGridState()).thenReturn("0".repeat(100));

        manager = new TurnManager(fleetManager, view, gameMode);
    }

    @Test
    void constructor_startsInSetupState() {
        //Manager should always start in setup
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
        manager.transitionToActiveTurn();

        assertTrue(manager.getCurrentState() instanceof ActiveTurnState);
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
        assertNotNull(manager.getOpponentGrid());
        assertEquals(10, manager.getOpponentGrid().getRow());
        assertEquals(10, manager.getOpponentGrid().getCol());
        assertEquals(fleetManager, manager.getFleetManager());
        assertEquals(view, manager.getView());
        assertEquals(gameMode, manager.getGameModeStrategy());
    }
}
