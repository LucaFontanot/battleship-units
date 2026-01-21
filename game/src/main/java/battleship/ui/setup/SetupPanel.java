package battleship.ui.setup;

import battleship.model.FleetManager;
import battleship.model.Orientation;
import battleship.model.ShipType;
import battleship.ui.grid.GridUI;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class SetupPanel extends JPanel implements PlacementContext {

    @Getter
    private final GridUI gridUI;
    private final JPanel shipPalette;
    @Getter
    private ShipType selectedShipType = null;
    @Getter
    private Orientation selectedOrientation = Orientation.HORIZONTAL_RIGHT;

    public SetupPanel(FleetManager fleetManager) {
        setLayout(new BorderLayout());

        gridUI = new GridUI(fleetManager, this);
        shipPalette = new JPanel();
        shipPalette.setLayout(new BoxLayout(shipPalette, BoxLayout.Y_AXIS));

        for (ShipType type : ShipType.values()) {
            JButton shipButton = new JButton(type.getName());

            shipButton.addActionListener(e -> {
                selectedShipType = type;
                highlightSelectedShipButton(shipButton);
            });

            shipPalette.add(shipButton);
        }

        JButton rotateButton = new JButton("Rotate");

        rotateButton.addActionListener(e -> {
            Orientation[] values = Orientation.values();
            int index = selectedOrientation.ordinal();
            selectedOrientation = values[(index + 1) % values.length];
        });

        shipPalette.add(Box.createVerticalStrut(10));
        shipPalette.add(rotateButton);

        add(shipPalette, BorderLayout.WEST);
        add(gridUI, BorderLayout.CENTER);

    }

    private void highlightSelectedShipButton(JButton selected) {
        for (Component c : shipPalette.getComponents()) {
            if (c instanceof JButton btn) {
                btn.setBorder(UIManager.getBorder("Button.border"));
            }
        }
        selected.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
    }
}

