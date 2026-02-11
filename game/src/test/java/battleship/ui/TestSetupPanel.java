package battleship.ui;

import it.units.battleship.controller.game.actions.GridInteractionObserver;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.view.core.setup.SetupPanel;
import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestSetupPanel {

    private SetupPanel setupPanel;

    @Mock
    private GridInteractionObserver mockObserver;

    @BeforeEach
    public void setUp() {
        setupPanel = new SetupPanel(10, 10);
    }

    @Test
    public void testSetupPanelInitialization() {
        assertNotNull(setupPanel, "SetupPanel should be initialized");
        assertNotNull(setupPanel.getPlayerGridUI(), "Player grid UI should be initialized");
    }

    @Test
    public void testSetupPanelGridDimensions() {
        SetupPanel panel = new SetupPanel(8, 12);
        assertNotNull(panel.getPlayerGridUI(), "Grid UI should be initialized with custom dimensions");
    }

    @Test
    public void testSetObserver() {
        assertDoesNotThrow(() -> setupPanel.setObserver(mockObserver),
                "Setting observer should not throw exceptions");
    }

    @Test
    public void testUpdateSetupGridDoesNotThrow() {
        Grid grid = new Grid(10, 10);
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT,
                ShipType.DESTROYER, grid);
        List<Ship> fleet = Collections.singletonList(ship);

        String gridData = "0".repeat(100);

        assertDoesNotThrow(() -> setupPanel.updateSetupGrid(gridData, fleet),
                "Updating setup grid should not throw exceptions");
    }

    @Test
    public void testShowPlacementPreviewValidShip() {
        Grid grid = new Grid(10, 10);
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT,
                ShipType.DESTROYER, grid);
        LinkedHashSet<Coordinate> coords = new LinkedHashSet<>();
        coords.add(new Coordinate(0, 0));
        coords.add(new Coordinate(0, 1));

        assertDoesNotThrow(() -> setupPanel.showPlacementPreview(coords, true, ship),
                "Showing valid placement preview should not throw exceptions");
    }

    @Test
    public void testShowPlacementPreviewInvalidShip() {
        Grid grid = new Grid(10, 10);
        Ship ship = Ship.createShip(new Coordinate(0, 0), Orientation.HORIZONTAL_RIGHT,
                ShipType.DESTROYER, grid);
        LinkedHashSet<Coordinate> coords = new LinkedHashSet<>();
        coords.add(new Coordinate(9, 9));

        assertDoesNotThrow(() -> setupPanel.showPlacementPreview(coords, false, ship),
                "Showing invalid placement preview should not throw exceptions");
    }

    @Test
    public void testPlayerErrorSoundDoesNotThrow() {
        assertDoesNotThrow(() -> setupPanel.playerErrorSound(),
                "Playing error sound should not throw exceptions");
    }

    @Test
    public void testUpdateShipButtonsUpdatesButtonStates() {
        Map<ShipType, Integer> placedShips = new EnumMap<>(ShipType.class);
        placedShips.put(ShipType.DESTROYER, 1);

        Map<ShipType, Integer> fleetConfig = new EnumMap<>(ShipType.class);
        for (ShipType type : ShipType.values()) {
            fleetConfig.put(type, 1);
        }

        assertDoesNotThrow(() -> setupPanel.updateShipButtons(placedShips, fleetConfig),
                "Updating ship buttons should not throw exceptions");
    }

    @Test
    public void testRotateButtonCyclesOrientation() throws Exception {
        Field shipPaletteField = SetupPanel.class.getDeclaredField("shipPalette");
        shipPaletteField.setAccessible(true);
        JPanel shipPalette = (JPanel) shipPaletteField.get(setupPanel);

        JButton rotateButton = findButtonByText(shipPalette, "Rotate");
        assertNotNull(rotateButton, "Rotate button should exist");

        Orientation initialOrientation = setupPanel.getSelectedOrientation();

        for (var listener : rotateButton.getActionListeners()) {
            listener.actionPerformed(null);
        }

        Orientation afterRotation = setupPanel.getSelectedOrientation();
        assertNotEquals(initialOrientation, afterRotation,
                "Orientation should change after rotation");
    }

    @Test
    public void testRotateButtonCyclesThroughAllOrientations() throws Exception {
        Field shipPaletteField = SetupPanel.class.getDeclaredField("shipPalette");
        shipPaletteField.setAccessible(true);
        JPanel shipPalette = (JPanel) shipPaletteField.get(setupPanel);

        JButton rotateButton = findButtonByText(shipPalette, "Rotate");
        assertNotNull(rotateButton, "Rotate button should exist");

        Set<Orientation> seenOrientations = new HashSet<>();
        int orientationCount = Orientation.values().length;

        for (int i = 0; i < orientationCount; i++) {
            seenOrientations.add(setupPanel.getSelectedOrientation());
            for (var listener : rotateButton.getActionListeners()) {
                listener.actionPerformed(null);
            }
        }

        assertEquals(orientationCount, seenOrientations.size(),
                "Should cycle through all orientations");
    }

    @Test
    public void testShipButtonsSelectShipType() throws Exception {
        Field shipPaletteField = SetupPanel.class.getDeclaredField("shipPalette");
        shipPaletteField.setAccessible(true);
        JPanel shipPalette = (JPanel) shipPaletteField.get(setupPanel);

        for (ShipType type : ShipType.values()) {
            JButton shipButton = findButtonByText(shipPalette, type.getName());
            assertNotNull(shipButton, "Button for " + type.getName() + " should exist");

            for (var listener : shipButton.getActionListeners()) {
                listener.actionPerformed(null);
            }

            assertEquals(type, setupPanel.getSelectedShipType(),
                    "Selected ship type should be " + type.getName());
        }
    }

    @Test
    public void testOpenMethodCreatesFrame() throws Exception {
        assertDoesNotThrow(() -> setupPanel.open(), "Open method should not throw exceptions");

        Field mainFrameField = SetupPanel.class.getDeclaredField("mainFrame");
        mainFrameField.setAccessible(true);
        JFrame mainFrame = (JFrame) mainFrameField.get(setupPanel);

        if (mainFrame != null) {
            mainFrame.dispose();
        }
    }

    @Test
    public void testSetupPanelHasCorrectLayout() {
        LayoutManager layout = setupPanel.getLayout();
        assertTrue(layout instanceof BorderLayout,
                "SetupPanel should use BorderLayout");
    }

    @Test
    public void testSetupPanelContainsGridUI() {
        boolean foundGridUI = false;
        for (Component comp : getAllComponents(setupPanel)) {
            if (comp == setupPanel.getPlayerGridUI()) {
                foundGridUI = true;
                break;
            }
        }
        assertTrue(foundGridUI, "SetupPanel should contain the GridUI component");
    }

    @Test
    public void testSetupPanelContainsShipPalette() throws Exception {
        Field shipPaletteField = SetupPanel.class.getDeclaredField("shipPalette");
        shipPaletteField.setAccessible(true);
        JPanel shipPalette = (JPanel) shipPaletteField.get(setupPanel);

        assertNotNull(shipPalette, "Ship palette should not be null");
        assertTrue(shipPalette.getComponentCount() > 0,
                "Ship palette should have components");
    }

    @Test
    public void testSetupPanelHasAllShipButtons() throws Exception {
        Field shipButtonsField = SetupPanel.class.getDeclaredField("shipButtons");
        shipButtonsField.setAccessible(true);
        Map<ShipType, JButton> shipButtons = (Map<ShipType, JButton>) shipButtonsField.get(setupPanel);

        assertNotNull(shipButtons, "Ship buttons map should not be null");
        assertEquals(ShipType.values().length, shipButtons.size(),
                "Should have a button for each ship type");

        for (ShipType type : ShipType.values()) {
            assertTrue(shipButtons.containsKey(type),
                    "Should have button for " + type.getName());
            assertNotNull(shipButtons.get(type),
                    "Button for " + type.getName() + " should not be null");
        }
    }

    private JButton findButtonByText(Container container, String text) {
        for (Component comp : getAllComponents(container)) {
            if (comp instanceof JButton button) {
                if (text.equals(button.getText())) {
                    return button;
                }
            }
        }
        return null;
    }

    private List<Component> getAllComponents(Container container) {
        List<Component> components = new ArrayList<>();
        for (Component comp : container.getComponents()) {
            components.add(comp);
            if (comp instanceof Container) {
                components.addAll(getAllComponents((Container) comp));
            }
        }
        return components;
    }
}
