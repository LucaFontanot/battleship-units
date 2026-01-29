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

public class SetupPanel extends JPanel implements PlacementContext, CellClickListener {

    @Getter
    private final GridUI gridUI;
    private final FleetManager fleetManager;

    private final JPanel shipPalette;

    @Getter
    private ShipType selectedShipType = null;
    @Getter
    private Orientation selectedOrientation = Orientation.HORIZONTAL_RIGHT;
    private static final Dimension BUTTON_SIZE = new Dimension(120, 30);

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
                highlightSelectedShipButton(shipButton);
            });

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
    }

    private void highlightSelectedShipButton(JButton selected) {
        for (Component c : shipPalette.getComponents()) {
            if (c instanceof JButton btn) {
                btn.setBorder(UIManager.getBorder("Button.border"));
            }
        }
        selected.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
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
        } catch (IllegalArgumentException ex) {
            Toolkit.getDefaultToolkit().beep();

            var coords = selectedShipType.getShipCoordinates(coordinate, selectedOrientation);
            gridUI.showPlacementPreview(coords, false, null);
        }
    }
}
