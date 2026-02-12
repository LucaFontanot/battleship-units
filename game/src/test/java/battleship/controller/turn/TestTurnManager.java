package battleship.controller.turn;

import battleship.testutil.FakeGameMode;
import battleship.testutil.FakeView;
import it.units.battleship.Coordinate;
import it.units.battleship.CellState;
import it.units.battleship.GameState;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import it.units.battleship.controller.turn.TurnManager;
import it.units.battleship.controller.turn.adapters.GameViewMediator;
import it.units.battleship.controller.turn.adapters.NetworkAdapter;
import it.units.battleship.controller.turn.states.*;
import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestTurnManager {

    private FakeView fakeView;
    private FakeGameMode fakeGameMode;
    private FleetManager fleetManager;
    private Grid playerGrid;
    private Grid opponentGrid;
    private TurnManager manager;

    @BeforeEach
    void setUp() {
        fakeView = new FakeView();
        fakeGameMode = new FakeGameMode();
        playerGrid = new Grid(10, 10);
        opponentGrid = new Grid(10, 10);
        fleetManager = new FleetManager(playerGrid, Map.of(ShipType.DESTROYER, 2));

        GameViewMediator viewActions = new GameViewMediator(fakeView, fleetManager, opponentGrid);
        NetworkAdapter networkActions = new NetworkAdapter(fakeGameMode);
        manager = new TurnManager(viewActions, networkActions, fleetManager, opponentGrid);
    }

    @Test
    void constructor_startsInSetupState() {
        assertNotNull(manager.getCurrentState());
        assertInstanceOf(SetupState.class, manager.getCurrentState());
        assertEquals(GameState.SETUP.name(), manager.getCurrentStateName());
    }

    @Test
    void start_entersInitialState_andNotifyView() {
        manager.start();

        assertTrue(fakeView.playerTurn);
        assertNotNull(fakeView.lastPlacedCounts);
    }

    @Test
    void transitionToActiveTurn_replacesCurrentState() {
        manager.transitionToActiveTurn();

        assertInstanceOf(ActiveTurnState.class, manager.getCurrentState());
        assertEquals(GameState.ACTIVE_TURN.name(), manager.getCurrentStateName());
    }

    @Test
    void transitionToActiveTurn_setsPlayerTurnTrue() {
        manager.transitionToActiveTurn();

        assertTrue(fakeView.playerTurn);
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
    void playerGridClick_inSetup_placesShipOnRealGrid() {
        fakeView.selectedShipType = ShipType.DESTROYER;
        fakeView.selectedOrientation = Orientation.HORIZONTAL_RIGHT;

        manager.start();
        manager.handlePlayerGridClick(new Coordinate(0, 0));

        // The ship was actually placed in the FleetManager
        assertEquals(1, fleetManager.getFleet().size());
        assertNotNull(fakeView.lastPlayerGridSerialized);
    }

    @Test
    void opponentGridClick_inActiveTurn_firesRealShot() {
        manager.transitionToActiveTurn();

        Coordinate target = new Coordinate(3, 3);
        manager.handleOpponentGridClick(target);

        // Shot was sended through the FakeGameMode
        assertEquals(target, fakeGameMode.lastShotSent);
        // State transitioned to WaitingOpponent
        assertInstanceOf(WaitingOpponentState.class, manager.getCurrentState());
    }

    @Test
    void opponentGridClick_onAlreadyShotCell_notifiesUser() {
        manager.transitionToActiveTurn();

        Coordinate target = new Coordinate(3, 3);
        opponentGrid.changeState(target, CellState.MISS);

        manager.handleOpponentGridClick(target);

        assertEquals("You already shot here!", fakeView.lastSystemMessage);
        // No shot should been fired
        assertNull(fakeGameMode.lastShotSent);
    }

    @Test
    void opponentGridHover_inActiveTurn_showPreview() {
        manager.transitionToActiveTurn();

        Coordinate c = new Coordinate(3, 5);
        manager.handleOpponentGridHover(c);

        assertEquals(c, fakeView.lastShotPreview);
    }

    @Test
    void incomingShot_miss_transitionsToActiveTurn() {
        // Place a ship and go into WaitingOpponent
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, playerGrid);
        fleetManager.addShip(ship);
        manager.transitionToWaitingOpponent();

        // Missed shot
        manager.handleIncomingShot(new Coordinate(5, 5));

        // FleetManager updated the real grid
        assertEquals(CellState.MISS, playerGrid.getState(new Coordinate(5, 5)));
        // Network received the grid update
        assertNotNull(fakeGameMode.lastSentGrid);
        assertFalse(fakeGameMode.lastSentShotOutcome);
        // Transition to ActiveTurn (is our turn now)
        assertInstanceOf(ActiveTurnState.class, manager.getCurrentState());
    }

    @Test
    void incomingShot_hit_transitionsToActiveTurn() {
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, playerGrid);
        fleetManager.addShip(ship);
        Ship ship2 = Ship.createShip(new Coordinate(5, 5), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, playerGrid);
        fleetManager.addShip(ship2);
        manager.transitionToWaitingOpponent();

        // Shot on the ship
        manager.handleIncomingShot(new Coordinate(0, 0));

        assertEquals(CellState.HIT, playerGrid.getState(new Coordinate(0, 0)));
        assertTrue(fakeGameMode.lastSentShotOutcome);
        // Game is not over because there is still another ship
        assertInstanceOf(ActiveTurnState.class, manager.getCurrentState());
    }

    @Test
    void incomingShot_sinksAllShips_transitionsToGameOver() {
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT, ShipType.DESTROYER, playerGrid);
        fleetManager.addShip(ship);
        manager.transitionToWaitingOpponent();

        // Sinks the entire fleet
        for (Coordinate coord: ship.getCoordinates()){
            manager.transitionToWaitingOpponent();
            manager.handleIncomingShot(coord);
        }

        assertInstanceOf(GameOverState.class, manager.getCurrentState());
    }

    @Test
    void opponentGridUpdate_isForwardedToView() {
        String gridData = "0".repeat(100);
        List<Ship> fleet = List.of();

        manager.handleOpponentGridUpdate(gridData, fleet);

        assertEquals(gridData, fakeView.lastOpponentGridSerialized);
        assertEquals(fleet, fakeView.lastOpponentFleet);
    }

    @Test
    void gameOver_forcesGameOverState() {
        manager.handleGameOver("You lost!");

        assertInstanceOf(GameOverState.class, manager.getCurrentState());
        assertEquals(GameState.GAME_OVER.name(), manager.getCurrentStateName());
    }

    @Test
    void handleGameOver_showsEndGameMessage() {
        manager.handleGameOver("Victory!");

        assertEquals("Victory!", fakeView.lastEndGameMessage);
    }

    @Test
    void gameStatusReceived_activeTurn_transitionCorrectly() {
        manager.transitionToWaitingSetup();

        manager.handleGameStatusReceived(GameState.ACTIVE_TURN);

        assertTrue(fakeView.transitionToGamePhaseCalled);
        assertInstanceOf(ActiveTurnState.class, manager.getCurrentState());
    }

    @Test
    void gameStatusReceived_waitingOpponent_transitionCorrectly() {
        manager.transitionToWaitingSetup();

        manager.handleGameStatusReceived(GameState.WAITING_FOR_OPPONENT);

        assertTrue(fakeView.transitionToGamePhaseCalled);
        assertInstanceOf(WaitingOpponentState.class, manager.getCurrentState());
    }

    @Test
    void placingAllShips_completesFleet_andTransitionToWaitingSetup() {
        fakeView.selectedShipType = ShipType.DESTROYER;
        fakeView.selectedOrientation = Orientation.HORIZONTAL_RIGHT;
        manager.start();

        // Place the first DESTROYER
        manager.handlePlayerGridClick(new Coordinate(0, 0));
        assertInstanceOf(SetupState.class, manager.getCurrentState());

        // Place second DESTROYER -> fleet is completed
        manager.handlePlayerGridClick(new Coordinate(3, 3));

        // Fleet is complete: transition to WaitingSetup
        assertInstanceOf(WaitingSetupState.class, manager.getCurrentState());
        assertTrue(fakeGameMode.setupCompleteNotified);
        assertEquals(2, fleetManager.getFleet().size());
    }
}
