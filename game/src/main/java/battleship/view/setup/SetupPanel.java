package battleship.view.setup;

import battleship.controller.game.GameController;
import battleship.controller.setup.SetupGridHandler;
import battleship.model.game.Ship;
import battleship.view.grid.GridUI;
import battleship.utils.DimensionsUtils;
import it.units.battleship.Coordinate;
import it.units.battleship.Logger;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class SetupPanel extends JPanel implements PlacementContext, SetupView {

    JFrame mainFrame;

    @Getter
    private final GridUI gridUI;

    private final JPanel shipPalette;

    @Getter
    private ShipType selectedShipType = null;

    private JButton readyButton;

    @Getter
    private Orientation selectedOrientation = Orientation.HORIZONTAL_RIGHT;

    private final Map<ShipType, JButton> shipButtons = new EnumMap<>(ShipType.class);

    private SetupGridHandler observer;

    public SetupPanel(GameController controller) {

        setLayout(new BorderLayout());
        gridUI = new GridUI(controller.getGrid().getRow(), controller.getGrid().getCol());
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

        // ready button
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        readyButton = new JButton("Ready!");
        readyButton.setEnabled(false);
        readyButton.setFocusPainted(false);

        readyButton.addActionListener(e -> {
            observer.requestSetupCompletion();
            readyButton.setEnabled(false);
            readyButton.setText("Waiting...");
        });

        bottomBar.add(readyButton);
        add(bottomBar, BorderLayout.SOUTH);
    }

    @Override
    public void showPlacementPreview(LinkedHashSet<Coordinate> coord, boolean validShip, Ship ship) {
        getGridUI().showPlacementPreview(coord, validShip, ship);
    }

    @Override
    public void playerErrorSound() {
        Toolkit.getDefaultToolkit().beep();
    }

    @Override
    public void open() {
        mainFrame = new JFrame("Battleship Setup");
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setContentPane(this);
        mainFrame.pack();
        mainFrame.setSize(DimensionsUtils.getScaledDimensions(800, 600));
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    void dispose() {
        if (mainFrame != null) {
            mainFrame.dispose();
        }
    }

    @Override
    public void updateSetupGrid(String gridSerialized, List<Ship> fleetToRender) {
        getGridUI().displayData(gridSerialized, fleetToRender);
    }

    @Override
    public void setObserver(SetupGridHandler observer) {
        Logger.debug("SetupPanel::setObserver");
        getGridUI().setObserver(observer);
        this.observer = observer;
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
    public void updateShipButtons(Map<ShipType, Integer> placedShip, Map<ShipType, Integer> fleetConfiguration) {

        boolean isFleetComplete = true;
        for (ShipType type : ShipType.values()) {
            JButton button = shipButtons.get(type);
            if (button == null) continue;

            int placed = placedShip.getOrDefault(type, 0);
            int total = fleetConfiguration.getOrDefault(type, 0);
            int remaining = total - placed;

            button.setText(type.getName() + " (" + remaining + "/" + total + ")");
            button.setEnabled(remaining > 0);

            if (placed < total) {
                isFleetComplete = false;
                continue;
            }

            if (selectedShipType == type && remaining <= 0) {
                clearShipSelection();
            }
        }
        shipPalette.revalidate();
        shipPalette.repaint();

        if (readyButton != null) {
            readyButton.setEnabled(isFleetComplete);
        }
    }

    private void clearShipSelection() {
        selectedShipType = null;
        for (JButton btn : shipButtons.values()) {
            btn.setBorder(UIManager.getBorder("Button.border"));
        }
    }
}
