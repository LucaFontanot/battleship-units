package battleship.ui;

import battleship.model.Orientation;
import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TextureLoader {

    static BufferedImage [][] textures = new BufferedImage[5][6];

    static {
        BufferedImage textureImage = loadTextureImage("texture.png");
        int tileWidth = textureImage.getWidth() / 6;
        int tileHeight = textureImage.getHeight() / 5;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                textures[i][j] = textureImage.getSubimage(j * tileWidth, i * tileHeight, tileWidth, tileHeight);
            }
        }
    }

    static BufferedImage loadTextureImage(String image) {
        try (InputStream is = TextureLoader.class.getResourceAsStream("/images/texture.png")) {
            if (is == null) {
                throw new RuntimeException("Image not found: " + image);
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + image, e);
        }
    }

    public static BufferedImage getTexture(int col, int row, int rotationAngle, boolean horizontalMirror) {
        Logger.debug("Getting texture at row " + row + ", col " + col + " with rotation " + rotationAngle);
        if (row < 0 || row >= textures.length || col < 0 || col >= textures[0].length) {
            throw new IndexOutOfBoundsException("Texture indices out of bounds");
        }
        BufferedImage original = textures[row][col];
        int w = original.getWidth();
        int h = original.getHeight();
        BufferedImage rotated = new BufferedImage(w, h, original.getType());
        java.awt.Graphics2D g2d = rotated.createGraphics();
        g2d.rotate(Math.toRadians(rotationAngle), w / 2.0, h / 2.0);
        if (horizontalMirror) {
            g2d.scale(-1, 1);
            g2d.translate(-w, 0);
        }
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();
        return rotated;
    }

    public static BufferedImage getTexture(int col, int row, int rotationAngle) {
        return getTexture(col, row, rotationAngle, false);
    }

    public static BufferedImage getTextureForShip(Ship ship, Coordinate coordinate){
        List<Coordinate> shipCoordinates = new ArrayList<>(ship.getCoordinates());
        Orientation orientation = ship.getOrientation();

        int foundIndex = -1;

        for (int i = 0; i < shipCoordinates.size(); i++) {
            if (shipCoordinates.get(i).equals(coordinate)) {
                foundIndex = i;
                break;
            }
        }

        if (foundIndex == -1) {
            return null;
        }

        int rotationAngle = switch (orientation) {
            case VERTICAL_DOWN -> 0;
            case HORIZONTAL_RIGHT -> 270;
            case VERTICAL_UP -> 180;
            case HORIZONTAL_LEFT -> 90;
        };

        switch (ship.getShipType()){
            case BATTLESHIP -> {
                return getTexture(0, foundIndex, rotationAngle);
            }
            case CRUISER -> {
                return getTexture(1, foundIndex, rotationAngle);
            }
            case FRIGATE -> {
                return getTexture(2, foundIndex, rotationAngle);
            }
            case DESTROYER -> {
                return getTexture(3, foundIndex, rotationAngle);
            }
            case CARRIER -> {
                return getTexture(4 + (foundIndex/4), foundIndex % 4, rotationAngle, true);
            }
        }

        return null;
    }
}
