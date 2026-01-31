package battleship.ui.setup;

import battleship.model.FleetManager;
import battleship.model.Orientation;
import battleship.model.Ship;
import battleship.model.ShipType;
import battleship.ui.grid.CellClickListener;
import battleship.ui.grid.GridUI;
import it.units.battleship.Coordinate;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class SetupPanel extends JPanel implements PlacementContext, CellClickListener {

    @Getter
    private final GridUI gridUI;
    private final FleetManager fleetManager;

    private final JPanel shipPalette;

    @Getter
    private ShipType selectedShipType = null;
    private JButton selectedShipButton = null;
    private JButton nextButton;

    @Getter
    private Orientation selectedOrientation = Orientation.HORIZONTAL_RIGHT;
    private static final Dimension BUTTON_SIZE = new Dimension(120, 30);

    private final Map<ShipType, JButton> shipButtons = new EnumMap<>(ShipType.class);

    public SetupPanel(FleetManager fleetManager) {
        this.fleetManager = fleetManager;

        setLayout(new BorderLayout());
        gridUI = new GridUI(fleetManager, this, this);
        shipPalette = new JPanel();
        shipPalette.setLayout(new BoxLayout(shipPalette, BoxLayout.Y_AXIS));
        shipPalette.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        shipPalette.setPreferredSize(new Dimension(160, 0));

        for (ShipType type : ShipType.values()) {
            JButton shipButton = new JButton(type.getName());

            shipButton.setPreferredSize(BUTTON_SIZE);
            shipButton.setMaximumSize(BUTTON_SIZE);
            shipButton.setMinimumSize(BUTTON_SIZE);
            shipButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            shipButton.setFocusPainted(false);

            shipButton.addActionListener(e -> {
                selectedShipType = type;
                selectedShipButton = shipButton;
                highlightSelectedShipButton(shipButton);
            });

            shipButtons.put(type, shipButton);

            shipPalette.add(shipButton);
            shipPalette.add(Box.createVerticalStrut(10));
        }

        JButton rotateButton = new JButton("Rotate");

        rotateButton.setPreferredSize(BUTTON_SIZE);
        rotateButton.setMaximumSize(BUTTON_SIZE);
        rotateButton.setMinimumSize(BUTTON_SIZE);
        rotateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rotateButton.setFocusPainted(false);

        rotateButton.addActionListener(e -> {
            Orientation[] values = Orientation.values();
            int index = selectedOrientation.ordinal();
            selectedOrientation = values[(index + 1) % values.length];
        });

        shipPalette.add(Box.createVerticalStrut(20));
        shipPalette.add(rotateButton);

        add(shipPalette, BorderLayout.WEST);
        JPanel gridWrapper = new JPanel(new GridBagLayout());
        gridWrapper.add(gridUI);

        add(gridWrapper, BorderLayout.CENTER);

        // next button
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        nextButton = new JButton("Next");
        nextButton.setEnabled(false);
        nextButton.setFocusPainted(false);

        nextButton.addActionListener(e -> {
            // TODO
        });

        bottomBar.add(nextButton);
        add(bottomBar, BorderLayout.SOUTH);

        updateShipButtons();
    }

    @Override
    public void onCellClicked(Coordinate coordinate) {
        if (selectedShipType == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        try {
            Ship ship = Ship.createShip(coordinate, selectedOrientation, selectedShipType, fleetManager.getGrid());
            boolean ok = fleetManager.addShip(ship);

            if (!ok) {
                Toolkit.getDefaultToolkit().beep();
                gridUI.showPlacementPreview(ship.getCoordinates(), false, ship);
                return;
            }

            gridUI.placeShip(ship);
            gridUI.clearPlacementPreview();
            updateShipButtons();
        } catch (IllegalArgumentException ex) {
            Toolkit.getDefaultToolkit().beep();

            var coords = selectedShipType.getShipCoordinates(coordinate, selectedOrientation);
            gridUI.showPlacementPreview(coords, false, null);
        }
    }

    private void highlightSelectedShipButton(JButton selected) {
        for (Component c : shipPalette.getComponents()) {
            if (c instanceof JButton btn) {
                btn.setBorder(UIManager.getBorder("Button.border"));
            }
        }
        selected.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
    }

    private void updateShipButtons() {
        for (ShipType type : ShipType.values()) {
            JButton btn = shipButtons.get(type);
            if (btn == null) continue;

            int remaining = fleetManager.getRemaining(type);
            int total = fleetManager.getRequiredCount(type);

            btn.setText(type.getName() + " (" + remaining + "/" + total + ")");
            btn.setEnabled(remaining > 0);

            if (selectedShipType == type && remaining <= 0) {
                clearShipSelection();
            }
        }
        shipPalette.revalidate();
        shipPalette.repaint();

        if (nextButton != null) {
            nextButton.setEnabled(fleetManager.isFleetComplete());
        }
    }

    private void clearShipSelection() {
        selectedShipType = null;
        selectedShipButton = null;
        for (JButton btn : shipButtons.values()) {
            btn.setBorder(UIManager.getBorder("Button.border"));
        }
    }
}
