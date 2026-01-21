package battleship.ui.grid;

import battleship.model.CellState;
import battleship.model.Grid;
import battleship.ui.setup.PlacementContext;
import it.units.battleship.Coordinate;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class CellPanel extends JLabel {

    static final double OPACITY = 0.5;
    static final Color HIT_COLOR = new Color(0xFF8000);
    static final Color MISS_COLOR = new Color(0x0000FF);
    static final Color EMPTY_COLOR = new Color(0xFFFFFF);
    static final Color SUNK_COLOR = new Color(0xFF0303);
    static final Color SELECT_VALID_COLOR = new Color(0xAAFFAA);
    static final Color SELECT_INVALID_COLOR = new Color(0xFFAAAA);
    static final Color SELECTED_COLOR = new Color(0xFFFF00);

    final Grid grid;
    final Coordinate coordinate;
    private Color baseColor; // colore base della cella
    private boolean preview = false;
    private boolean previewValid = true;
    @Getter
    private boolean selected = false;

    @Setter
    private CellHoverListener hoverListener;
    @Setter
    private CellClickListener clickListener;


    public CellPanel(Grid grid, Coordinate coordinate) {
        this.grid = grid;
        this.coordinate = coordinate;

        setOpaque(false);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        setFocusable(true);
        refresh();

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (hoverListener != null) {
                    hoverListener.onCellHover(coordinate);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (hoverListener != null) {
                    hoverListener.onCellExit();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (clickListener != null) {
                    clickListener.onCellClicked(coordinate);
                }
            }

        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(40, 40);
    }

    private Color applyOpacity(Color color, double opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(opacity * 255));
    }

    public void addTexture(BufferedImage texture) {
        setIcon(new ImageIcon(texture.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH)));
    }

    public void removeTexture() {
        setIcon(null);
    }

    public void refresh() {
        CellState state = grid.getState(coordinate);
        switch (state) {
            case EMPTY -> baseColor = EMPTY_COLOR;
            case HIT -> baseColor = HIT_COLOR;
            case MISS -> baseColor = MISS_COLOR;
            case SUNK -> baseColor = SUNK_COLOR;
            default -> baseColor = EMPTY_COLOR;
        }
        setBackground(applyOpacity(baseColor, OPACITY));
        repaint();
    }

    public void setPreview(boolean preview, boolean valid) {
        this.preview = preview;
        this.previewValid = valid;
        repaint();
    }


    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(baseColor);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (selected) {
            g2.setColor(SELECTED_COLOR);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        if (preview) {
            g2.setColor(previewValid ? SELECT_VALID_COLOR : SELECT_INVALID_COLOR);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.dispose();
        super.paintComponent(g);
    }

}