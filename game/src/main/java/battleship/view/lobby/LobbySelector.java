package battleship.view.lobby;

import battleship.controller.lobby.LobbyController;
import com.google.gson.Gson;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import it.units.battleship.Logger;
import it.units.battleship.data.LobbiesResponseData;
import it.units.battleship.data.LobbyData;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LobbySelector {
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel lobbiesPanel;
    private JTextField lobbyNamefield;
    private JButton createLobbyButton;

    private final LobbyController controller;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public LobbySelector(LobbyController controller) {
        this.controller = controller;

        createLobbyButton.addActionListener(e -> {
            String lobbyName = lobbyNamefield.getText().trim();
            if (lobbyName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Lobby name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String name = promptForName();
            LobbyData createdLobby = controller.createLobby(lobbyName, name);
            if (createdLobby != null) {
                handleLobbyJoin(createdLobby, name);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to create lobby", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public String promptForName() {
        return JOptionPane.showInputDialog(frame, "Enter your name:", "Name", JOptionPane.PLAIN_MESSAGE);
    }

    public void handleLobbyJoin(LobbyData lobby, String name) {
        if(controller.connectLobbyWebsocket(lobby, name)){
            if(frame != null) frame.dispose();
        } else {
            JOptionPane.showMessageDialog(frame, "Failed to join lobby", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void show() {
        frame = new JFrame("Lobby Selector");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                cleanup();
            }
        });
        frame.pack();
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        scheduler.scheduleAtFixedRate(this::fetchAndRenderLobbies, 0, 5, TimeUnit.SECONDS);
    }

    public void cleanup() {
        scheduler.shutdownNow();
    }

    JPanel getLobbySelectorPanel(LobbyData lobby) {
        System.out.println("Rendering lobby: " + new Gson().toJson(lobby));
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        Spacer spacer = new Spacer();
        panel.add(spacer, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(descriptionPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        JLabel lobbyName = new JLabel();
        lobbyName.setText(lobby.getLobbyName());
        descriptionPanel.add(lobbyName, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        JLabel player1Name = new JLabel();
        player1Name.setText("Player1: " + lobby.getPlayerOne());
        descriptionPanel.add(player1Name, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        JButton joinButton = new JButton();
        joinButton.setText("JOIN");
        joinButton.addActionListener(e -> {
            String name = promptForName();
            if (name.equalsIgnoreCase(lobby.getPlayerOne())) {
                JOptionPane.showMessageDialog(frame, "Name already taken in this lobby, please choose another name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            handleLobbyJoin(lobby, name);
        });
        panel.add(joinButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        return panel;
    }

    void renderLobbies(java.util.List<? extends LobbyData> lobbies) {
        lobbiesPanel.removeAll();
        lobbiesPanel.setLayout(new GridLayoutManager(lobbies.size() + 1, 1, new Insets(0, 0, 0, 0), -1, -1));
        int i = 0;
        for (LobbyData lobby : lobbies) {
            lobbiesPanel.add(getLobbySelectorPanel(lobby), new GridConstraints(i++, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        }
        lobbiesPanel.add(new Spacer(), new GridConstraints(i++, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        lobbiesPanel.revalidate();
        lobbiesPanel.repaint();
    }

    void fetchAndRenderLobbies() {
        LobbiesResponseData lobbiesResponseData = controller.getLobbies();
        if (lobbiesResponseData == null) {
            Logger.warn("Failed to fetch lobbies, response data is null");
            return;
        }
        renderLobbies(lobbiesResponseData.getResults());
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
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(1, 2, new Insets(5, 5, 5, 5), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, -1, 18, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Join an existing Game");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        lobbiesPanel = new JPanel();
        lobbiesPanel.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        scrollPane1.setViewportView(lobbiesPanel);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$(null, -1, 18, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("Create a new Lobby");
        panel2.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Lobby name");
        panel2.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lobbyNamefield = new JTextField();
        panel2.add(lobbyNamefield, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        createLobbyButton = new JButton();
        createLobbyButton.setText("CREATE");
        panel2.add(createLobbyButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
