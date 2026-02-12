package battleship.ui;

import it.units.battleship.controller.game.actions.GridInteractionObserver;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.view.core.BattleshipFrame;
import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestBattleshipFrame {

    private BattleshipFrame battleshipFrame;

    @Mock
    private GridInteractionObserver mockPlayerObserver;

    @Mock
    private GridInteractionObserver mockOpponentObserver;

    @BeforeEach
    public void setUp() {
        battleshipFrame = new BattleshipFrame(10, 10);
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (battleshipFrame != null) {
            battleshipFrame.dispose();
        }
    }

    @Test
    public void testBattleshipFrameInitialization() {
        assertNotNull(battleshipFrame, "BattleshipFrame should be initialized");
        assertEquals("Battleship", battleshipFrame.getTitle(), "Frame title should be 'Battleship'");
    }

    @Test
    public void testBattleshipFrameCustomDimensions() {
        BattleshipFrame frame = new BattleshipFrame(8, 12);
        assertNotNull(frame, "BattleshipFrame should be initialized with custom dimensions");
        frame.dispose();
    }

    @Test
    public void testSetPlayerGridListenerDoesNotThrow() {
        assertDoesNotThrow(() -> battleshipFrame.setPlayerGridListener(mockPlayerObserver),
                "Setting player grid listener should not throw exceptions");
    }

    @Test
    public void testSetOpponentGridListenerDoesNotThrow() {
        assertDoesNotThrow(() -> battleshipFrame.setOpponentGridListener(mockOpponentObserver),
                "Setting opponent grid listener should not throw exceptions");
    }

    @Test
    public void testOpenMethodShowsFrame() {
        assertDoesNotThrow(() -> battleshipFrame.open(),
                "Open method should not throw exceptions");
        assertTrue(battleshipFrame.isVisible(), "Frame should be visible after open()");
    }

    @Test
    public void testDisposeMethodClosesFrame() {
        battleshipFrame.open();
        battleshipFrame.dispose();
        assertFalse(battleshipFrame.isDisplayable(), "Frame should not be displayable after dispose()");
    }

    @Test
    public void testGetSelectedShipTypeReturnsValue() {
        assertDoesNotThrow(() -> battleshipFrame.getSelectedShipType(),
                "Getting selected ship type should not throw exceptions");
    }

    @Test
    public void testGetSelectedOrientationReturnsValue() {
        Orientation orientation = battleshipFrame.getSelectedOrientation();
        assertNotNull(orientation, "Selected orientation should not be null");
    }

    @Test
    public void testShowPlacementPreviewValidShip() {
        Grid grid = new Grid(10, 10);
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT,
                ShipType.DESTROYER, grid);
        LinkedHashSet<Coordinate> coords = new LinkedHashSet<>();
        coords.add(new Coordinate(0, 0));
        coords.add(new Coordinate(0, 1));

        assertDoesNotThrow(() -> battleshipFrame.showPlacementPreview(coords, true, ship),
                "Showing valid placement preview should not throw exceptions");
    }

    @Test
    public void testShowPlacementPreviewInvalidShip() {
        Grid grid = new Grid(10, 10);
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT,
                ShipType.DESTROYER, grid);
        LinkedHashSet<Coordinate> coords = new LinkedHashSet<>();
        coords.add(new Coordinate(9, 9));

        assertDoesNotThrow(() -> battleshipFrame.showPlacementPreview(coords, false, ship),
                "Showing invalid placement preview should not throw exceptions");
    }

    @Test
    public void testUpdatePlayerGridDoesNotThrow() {
        String gridData = "0".repeat(100);
        List<Ship> fleet = Collections.emptyList();

        assertDoesNotThrow(() -> battleshipFrame.updatePlayerGrid(gridData, fleet),
                "Updating player grid should not throw exceptions");
    }

    @Test
    public void testRefreshFleetSelectionDoesNotThrow() {
        Map<ShipType, Integer> placedShips = new EnumMap<>(ShipType.class);
        Map<ShipType, Integer> fleetConfig = new EnumMap<>(ShipType.class);

        for (ShipType type : ShipType.values()) {
            fleetConfig.put(type, 1);
        }

        assertDoesNotThrow(() -> battleshipFrame.refreshFleetSelection(placedShips, fleetConfig),
                "Refreshing fleet selection should not throw exceptions");
    }

    @Test
    public void testTransitionToGamePhaseDoesNotThrow() {
        assertDoesNotThrow(() -> battleshipFrame.transitionToGamePhase(),
                "Transitioning to game phase should not throw exceptions");
    }

    @Test
    public void testShowWaitingForOpponentDoesNotThrow() {
        assertDoesNotThrow(() -> battleshipFrame.showWaitingForOpponent("Waiting..."),
                "Showing waiting for opponent should not throw exceptions");
    }

    @Test
    public void testUpdateOpponentGridDoesNotThrow() {
        String gridData = "0".repeat(100);
        List<Ship> fleet = Collections.emptyList();

        assertDoesNotThrow(() -> battleshipFrame.updateOpponentGrid(gridData, fleet),
                "Updating opponent grid should not throw exceptions");
    }

    @Test
    public void testSetPlayerTurnDoesNotThrow() {
        assertDoesNotThrow(() -> battleshipFrame.setPlayerTurn(true),
                "Setting player turn to true should not throw exceptions");
        assertDoesNotThrow(() -> battleshipFrame.setPlayerTurn(false),
                "Setting player turn to false should not throw exceptions");
    }

    @Test
    public void testShowShotPreviewDoesNotThrow() {
        Coordinate coord = new Coordinate(5, 5);
        assertDoesNotThrow(() -> battleshipFrame.showShotPreview(coord),
                "Showing shot preview should not throw exceptions");
    }

    @Test
    public void testShowEndGamePhaseDoesNotThrow() {
        assertDoesNotThrow(() -> battleshipFrame.showEndGamePhase("Game Over!"),
                "Showing end game phase should not throw exceptions");
    }

    @Test
    public void testShowSystemMessageDoesNotThrow() {
        assertDoesNotThrow(() -> battleshipFrame.showSystemMessage("Test message"),
                "Showing system message should not throw exceptions");
    }

    @Test
    public void testPlayerErrorSoundDoesNotThrow() {
        assertDoesNotThrow(() -> battleshipFrame.playerErrorSound(),
                "Playing error sound should not throw exceptions");
    }

    @Test
    public void testSetReturnToMenuActionDoesNotThrow() {
        Runnable mockAction = mock(Runnable.class);
        assertDoesNotThrow(() -> battleshipFrame.setReturnToMenuAction(mockAction),
                "Setting return to menu action should not throw exceptions");
    }

    @Test
    public void testSetReturnToMenuVisibleDoesNotThrow() {
        assertDoesNotThrow(() -> battleshipFrame.setReturnToMenuVisible(true),
                "Setting return to menu visible to true should not throw exceptions");
        assertDoesNotThrow(() -> battleshipFrame.setReturnToMenuVisible(false),
                "Setting return to menu visible to false should not throw exceptions");
    }

    @Test
    public void testPhaseTransitionsSetupToGame() {
        assertDoesNotThrow(() -> {
            Grid grid = new Grid(10, 10);
            Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT,
                    ShipType.DESTROYER, grid);
            List<Ship> fleet = Collections.singletonList(ship);
            String gridData = "0".repeat(100);

            battleshipFrame.updatePlayerGrid(gridData, fleet);
            battleshipFrame.transitionToGamePhase();
            battleshipFrame.updatePlayerGrid(gridData, fleet);
        }, "Phase transition from setup to game should not throw exceptions");
    }

    @Test
    public void testPhaseTransitionsSetupToWaitingToGame() {
        assertDoesNotThrow(() -> {
            battleshipFrame.showWaitingForOpponent("Waiting for opponent...");
            battleshipFrame.transitionToGamePhase();
        }, "Phase transition from setup to waiting to game should not throw exceptions");
    }

    @Test
    public void testMultipleGridUpdatesDoesNotThrow() {
        Grid grid = new Grid(10, 10);
        Ship ship1 = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT,
                ShipType.DESTROYER, grid);
        Ship ship2 = Ship.createShip(new Coordinate(2, 2), Orientation.VERTICAL_DOWN,
                ShipType.BATTLESHIP, grid);
        List<Ship> fleet = Arrays.asList(ship1, ship2);
        String gridData = "0".repeat(100);

        assertDoesNotThrow(() -> {
            battleshipFrame.updatePlayerGrid(gridData, fleet);
            battleshipFrame.transitionToGamePhase();
            battleshipFrame.updatePlayerGrid(gridData, fleet);
            battleshipFrame.updateOpponentGrid(gridData, fleet);
        }, "Multiple grid updates should not throw exceptions");
    }

    @Test
    public void testFrameDefaultCloseOperation() {
        assertEquals(javax.swing.WindowConstants.EXIT_ON_CLOSE,
                battleshipFrame.getDefaultCloseOperation(),
                "Default close operation should be EXIT_ON_CLOSE");
    }
}
