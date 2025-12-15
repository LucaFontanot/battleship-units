package battleship.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {
    static BufferedImage loadClassPathImage(String image) {
        try (InputStream is = ImageLoader.class.getResourceAsStream("/images/" + image)) {
            if (is == null) {
                throw new RuntimeException("Image not found: " + image);
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + image, e);
        }
    }

    static BufferedImage scaleImage(BufferedImage img, int width, int height) {
        Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bufferedScaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedScaledImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        return bufferedScaledImage;
    }

    static BufferedImage getAcceptableImageSize(BufferedImage img, int maxWidth, int maxHeight) {
        int width = img.getWidth();
        int height = img.getHeight();
        if (width <= maxWidth && height <= maxHeight) {
            return img;
        }
        double widthRatio = (double) maxWidth / width;
        double heightRatio = (double) maxHeight / height;
        double scale = Math.min(widthRatio, heightRatio);
        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);
        return scaleImage(img, newWidth, newHeight);
    }

    public static void loadImageIntoLabel(String image, JLabel label, int maxWidth, int maxHeight) {
        BufferedImage img = loadClassPathImage(image);
        BufferedImage acceptableImg = getAcceptableImageSize(img, maxWidth, maxHeight);
        label.setIcon(new ImageIcon(acceptableImg));
    }

    public static void loadImageIntoButton(String image, JButton button, int maxWidth, int maxHeight) {
        BufferedImage img = loadClassPathImage(image);
        BufferedImage acceptableImg = getAcceptableImageSize(img, maxWidth, maxHeight);
        button.setIcon(new ImageIcon(acceptableImg));
    }
}
