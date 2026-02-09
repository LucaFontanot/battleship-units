package battleship.view.welcome;

import battleship.controller.GameController;
import battleship.controller.lobby.LobbyController;
import battleship.controller.mode.GameModeStrategy;
import battleship.controller.mode.OnlineMultiplayerStrategy;
import battleship.controller.mode.SinglePlayerStrategy;
import battleship.model.game.FleetManager;
import battleship.model.game.Grid;
import battleship.view.BattleshipFrame;
import battleship.view.BattleshipView;

import battleship.view.lobby.LobbySelector;
import battleship.utils.DimensionsUtils;
import battleship.utils.ImageLoader;
import battleship.utils.ThemeSelector;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import it.units.battleship.Logger;
import it.units.battleship.data.LobbyData;

import javax.swing.*;
import java.awt.*;

import static it.units.battleship.Defaults.*;

public class WelcomeUi implements WelcomeUiActions {
    final JFrame frame = new JFrame("Battleship - Welcome");
    private JLabel logo;
    private JPanel root;
    private JButton singlePlayerButton;
    private JButton multiplayerOnlineButton;
    private JButton themeLight;
    private JButton themeDark;
    private JButton themeDarkLight;

    public WelcomeUi() {
        ImageLoader.loadImageIntoLabel("logo.png", logo, 200, 200);
        ImageLoader.loadImageIntoButton("theme-light.png", themeLight, 32, 32);
        themeDarkLight.addActionListener(e -> onThemeAutoSelected());
        ImageLoader.loadImageIntoButton("theme-dark.png", themeDark, 32, 32);
        themeDark.addActionListener(e -> onThemeDarkSelected());
        ImageLoader.loadImageIntoButton("theme-auto.png", themeDarkLight, 32, 32);
        themeLight.addActionListener(e -> onThemeLightSelected());
        singlePlayerButton.addActionListener(e -> onSinglePlayerSelected());
        multiplayerOnlineButton.addActionListener(e -> onOnlineMultiplayerSelected());
        Logger.debug("WelcomeUI::Initialized");
    }

    public void show() {
        Logger.debug("WelcomeUI::Show");
        frame.setContentPane(this.$$$getRootComponent$$$());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setSize(DimensionsUtils.getScaledDimensions(500, 450));
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public void dispose() {
        Logger.debug("WelcomeUI::Dispose");
        frame.dispose();
    }

    @Override
    public void onSinglePlayerSelected() {
        frame.dispose();

        Grid playerGrid = new Grid(GRID_ROWS, GRID_COLS);
        FleetManager fleetManager = new FleetManager(playerGrid, FLEET_CONFIGURATION);

        // Use the new unified approach
        GameModeStrategy singlePlayerMode = new SinglePlayerStrategy(FLEET_CONFIGURATION);
        BattleshipView view = new BattleshipFrame(GRID_ROWS, GRID_COLS);

        GameController controller = new GameController(
                playerGrid,
                fleetManager,
                singlePlayerMode,
                view
        );

        controller.startGame();
    }

    /*
    @Override                                                                                                                                        │
│  89       public void onLocalMultiplayerSelected() {                                                                                                       │
│  86 -         String[] options = {"Host (Player 1)", "Join (Player 2)"};                                                                                   │
│  87 -         int choice = JOptionPane.showOptionDialog(                                                                                                   │
│  88 -                 frame,                                                                                                                               │
│  89 -                 "Choose your role:",                                                                                                                 │
│  90 -                 "Local Multiplayer",                                                                                                                 │
│  91 -                 JOptionPane.DEFAULT_OPTION,                                                                                                          │
│  92 -                 JOptionPane.QUESTION_MESSAGE,                                                                                                        │
│  93 -                 null,                                                                                                                                │
│  94 -                 options,                                                                                                                             │
│  95 -                 options[0]                                                                                                                           │
│  96 -         );                                                                                                                                           │
│  97 -                                                                                                                                                      │
│  98 -         if (choice < 0) return;                                                                                                                      │
│  99 -                                                                                                                                                      │
│ 100 -         boolean isHost = (choice == 0);                                                                                                              │
│ 101 -         int port = isHost ? 8081 : 8081;                                                                                                             │
│ 102 -         String serverUri = "ws://localhost:" + port + it.units.battleship.Defaults.HTTP_LOBBY_PATH;                                                  │
│ 103 -                                                                                                                                                      │
│ 104 -         frame.dispose();                                                                                                                             │
│ 105 -                                                                                                                                                      │
│ 106 -         Grid playerGrid = new Grid(10, 10);                                                                                                          │
│ 107 -         Map<ShipType, Integer> fleetConfiguration = Map.of(                                                                                          │
│ 108 -                 ShipType.DESTROYER, 2,                                                                                                               │
│ 109 -                 ShipType.FRIGATE, 2,                                                                                                                 │
│ 110 -                 ShipType.CRUISER, 1,                                                                                                                 │
│ 111 -                 ShipType.BATTLESHIP, 1,                                                                                                              │
│ 112 -                 ShipType.CARRIER, 1                                                                                                                  │
│ 113 -         );                                                                                                                                           │
│ 114 -                                                                                                                                                      │
│ 115 -         FleetManager fleetManager = new FleetManager(playerGrid, fleetConfiguration);                                                                │
│ 116 -         GamePanel gameFrame = new GamePanel();                                                                                                       │
│ 117 -                                                                                                                                                      │
│ 118 -         GameModeStrategy gameMode = new LocalMultiplayerStrategy(serverUri, isHost);                                                                 │
│ 119 -                                                                                                                                                      │
│ 120 -         GameController controller = new GameController(                                                                                              │
│ 121 -                 playerGrid,                                                                                                                          │
│ 122 -                 fleetManager,                                                                                                                        │
│ 123 -                 gameMode,                                                                                                                            │
│ 124 -                 gameFrame                                                                                                                            │
│ 125 -         );                                                                                                                                           │
│ 126 -                                                                                                                                                      │
│ 127 -         gameFrame.open();                                                                                                                            │
│ 128 -         controller.startGame();                                                                                                                      │
│  90 +         // ... (Local multiplayer logic could be updated similarly if needed)                                                                        │
│  91       }
    */
    @Override
    public void onOnlineMultiplayerSelected() {
        dispose();
        LobbySelector lobbySelector = new LobbySelector(new LobbyController((client) -> {
            Logger.debug("WelcomeUI::LobbySelected - Client ready for online multiplayer");
            Grid playerGrid = new Grid(GRID_ROWS, GRID_COLS);
            FleetManager fleetManager = new FleetManager(playerGrid, FLEET_CONFIGURATION);

            GameModeStrategy onlineMode = new OnlineMultiplayerStrategy(client);
            BattleshipView view = new BattleshipFrame(GRID_ROWS, GRID_COLS);

            GameController controller = new GameController(
                    playerGrid,
                    fleetManager,
                    onlineMode,
                    view
            );

            controller.startGame();
        }));
        lobbySelector.show();
    }


    @Override
    public void onThemeAutoSelected() {
        Logger.debug("WelcomeUI::ThemeAutoSelected");
        ThemeSelector.selectAutomaticTheme();
        SwingUtilities.updateComponentTreeUI(frame);
    }

    @Override
    public void onThemeLightSelected() {
        Logger.log("WelcomeUI::ThemeLightSelected");
        ThemeSelector.selectLightTheme();
        SwingUtilities.updateComponentTreeUI(frame);
    }

    @Override
    public void onThemeDarkSelected() {
        Logger.log("WelcomeUI::ThemeDarkSelected");
        ThemeSelector.selectDarkTheme();
        SwingUtilities.updateComponentTreeUI(frame);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        root = new JPanel();
        root.setLayout(new GridLayoutManager(5, 3, new Insets(5, 5, 5, 5), -1, -1));
        logo = new JLabel();
        logo.setText("");
        root.add(logo, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 100), null, null, 1, false));
        final Spacer spacer1 = new Spacer();
        root.add(spacer1, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        root.add(spacer2, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        root.add(spacer3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        root.add(spacer4, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(20, 0, 0, 0), -1, -1));
        root.add(panel1, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        singlePlayerButton = new JButton();
        singlePlayerButton.setText("Singleplayer");
        panel1.add(singlePlayerButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 30), null, 0, false));
        multiplayerOnlineButton = new JButton();
        multiplayerOnlineButton.setText("Multiplayer");
        panel1.add(multiplayerOnlineButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 30), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        root.add(panel2, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel2.add(spacer5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        panel2.add(spacer6, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        themeLight = new JButton();
        themeLight.setText("");
        panel2.add(themeLight, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        themeDark = new JButton();
        themeDark.setText("");
        panel2.add(themeDark, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        themeDarkLight = new JButton();
        themeDarkLight.setText("");
        panel2.add(themeDarkLight, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
