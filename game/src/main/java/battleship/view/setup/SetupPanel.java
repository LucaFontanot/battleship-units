package battleship.view.setup;

import battleship.controller.actions.GridInteractionObserver;
import battleship.view.grid.GridUI;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class SetupPanel extends JPanel implements PlacementContext {

    @Getter
    private final GridUI gridUI;

    private final JPanel shipPalette;

    @Getter
    private ShipType selectedShipType = null;
    private JButton selectedShipButton = null;
    private JButton nextButton;

    @Getter
    private Orientation selectedOrientation = Orientation.HORIZONTAL_RIGHT;

    private final Map<ShipType, JButton> shipButtons = new EnumMap<>(ShipType.class);

    public SetupPanel() {

        setLayout(new BorderLayout());
        gridUI = new GridUI(10,10,null, this);
        shipPalette = new JPanel(new GridBagLayout());
        shipPalette.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);

        for (ShipType type : ShipType.values()) {
            JButton shipButton = new JButton(type.getName());

            shipButton.setFocusPainted(false);
            shipButton.setMargin(new Insets(8, 15, 8, 15));

            shipButton.addActionListener(e -> {
                selectedShipType = type;
                selectedShipButton = shipButton;
                highlightSelectedShipButton(shipButton);
            });

            shipButtons.put(type, shipButton);

            shipPalette.add(shipButton, gbc);
            gbc.gridy++;
        }

        JButton rotateButton = new JButton("Rotate");

        rotateButton.setFocusPainted(false);
        rotateButton.setMargin(new Insets(8, 15, 8, 15));

        rotateButton.addActionListener(e -> {
            Orientation[] values = Orientation.values();
            int index = selectedOrientation.ordinal();
            selectedOrientation = values[(index + 1) % values.length];
        });

        gbc.insets = new Insets(10, 0, 0, 0);
        shipPalette.add(rotateButton, gbc);

        // spacer to push buttons to the top
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        shipPalette.add(Box.createGlue(), gbc);

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
    }

    public void setGridInputListener(GridInteractionObserver observer){
        if (gridUI != null){
            gridUI.setObserver(observer);
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

    public void updateShipButtons(Map<ShipType, Integer> placedShip,
                                  Map<ShipType, Integer> fleetConfiguration) {

        boolean isFleetComplete = true;
        for (ShipType type : ShipType.values()) {
            JButton button = shipButtons.get(type);
            if (button == null) continue;

            int placed = placedShip.getOrDefault(type, 0);
            int total = fleetConfiguration.getOrDefault(type, 0);
            int remaining = total - placed;

            button.setText(type.getName() + " (" + remaining + "/" + total + ")");
            button.setEnabled(remaining > 0);

            if (placed < total){
                isFleetComplete = false;
                continue;
            }

            if (selectedShipType == type && remaining <= 0) {
                clearShipSelection();
            }
        }
        shipPalette.revalidate();
        shipPalette.repaint();

        if (nextButton != null) {
            nextButton.setEnabled(isFleetComplete);
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
