package battleship.view.grid;

import battleship.model.CellState;
import it.units.battleship.Coordinate;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Represents a single interactive cell within the Battleship grid.
 *
 * Visually, it can display a background color corresponding to its state (Hit, Miss, Empty)
 * and an optional texture overlay (e.g., a part of a ship).
 * Functionally, it acts as an input source, capturing mouse clicks and notifying
 * registered listeners with its.
 *
 */
public class CellPanel extends JLabel {

    static final double OPACITY = 0.5;
    static final Color HIT_COLOR = new Color(0xFF8000);
    static final Color MISS_COLOR = new Color(0x0000FF);
    static final Color EMPTY_COLOR = new Color(0xFFFFFF);
    static final Color SUNK_COLOR = new Color(0xFF0303);
    static final Color PREVIEW_VALID_COLOR = new Color(0xAAFFAA);
    static final Color PREVIEW_INVALID_COLOR = new Color(0xFFAAAA);
    static final Color SELECTED_COLOR = new Color(0xFFFF00);

    private CellState currentState;
    final Coordinate coordinate;
    private Color baseColor; // colore base della cella
    private boolean preview = false;
    private boolean previewValid = true;
    @Getter
    private boolean selected = false;

    private BufferedImage overlayTexture = null;
    private float overlayAlpha = 1.0f;

    @Setter
    private CellInteractionListener cellListener;


    public CellPanel(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.currentState = CellState.EMPTY;
        setOpaque(false);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        setFocusable(true);
        refresh();

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (cellListener != null) {
                    cellListener.onCellHover(coordinate);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (cellListener != null) {
                    cellListener.onCellExit();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (cellListener != null) {
                    cellListener.onCellClicked(coordinate);
                }
            }}
        );
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(40, 40);
    }

    private Color applyOpacity(Color color, double opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(opacity * 255));
    }

    public void addTexture(BufferedImage texture) {
        int w = Math.max(getWidth(), getPreferredSize().width);
        int h = Math.max(getHeight(), getPreferredSize().height);
        setIcon(new ImageIcon(texture.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
    }

    public void removeTexture() {
        setIcon(null);
    }

    public void updateState(CellState newState){
        this.currentState = newState;
        refresh();
    }

    public void refresh() {
        switch (currentState) {
            case EMPTY -> baseColor = EMPTY_COLOR;
            case HIT -> baseColor = HIT_COLOR;
            case MISS -> baseColor = MISS_COLOR;
            case SUNK -> baseColor = SUNK_COLOR;
            default -> baseColor = EMPTY_COLOR;
        }
        setBackground(applyOpacity(baseColor, OPACITY));
        repaint();
    }

    public CellState getCurrentState() {
        return currentState;
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

        // background
        g2.setColor(baseColor);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // ship placed (selected)
        if (selected) {
            g2.setColor(SELECTED_COLOR);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // preview (green/red)
        if (preview) {
            g2.setColor(previewValid ? PREVIEW_VALID_COLOR : PREVIEW_INVALID_COLOR);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // overlay texture (hover ship)
        if (overlayTexture != null) {
            Composite old = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayAlpha));
            g2.drawImage(overlayTexture, 0, 0, getWidth(), getHeight(), null);
            g2.setComposite(old);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    public void setOverlayTexture(BufferedImage img, float alpha) {
        this.overlayTexture = img;
        this.overlayAlpha = Math.max(0f, Math.min(1f, alpha));
        repaint();
    }

    public void clearOverlayTexture() {
        this.overlayTexture = null;
        repaint();
    }

}