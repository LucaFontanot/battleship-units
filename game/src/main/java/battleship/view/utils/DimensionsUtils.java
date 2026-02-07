package battleship.view.utils;

import java.awt.*;

public class DimensionsUtils {

    /**
     * Uses dimensions from a standard 1920x1080 screen to calculate scaled dimensions for the current screen size.
     *
     * @param width width based on a 1920x1080 screen
     * @param height height based on a 1920x1080 screen
     */
    public static Dimension getScaledDimensions(int width, int height) {
        double screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();

        double widthScale = screenWidth / 1920;
        double heightScale = screenHeight / 1080;

        int scaledWidth = (int) (width * widthScale);
        int scaledHeight = (int) (height * heightScale);

        return new Dimension(scaledWidth, scaledHeight);
    }
}
