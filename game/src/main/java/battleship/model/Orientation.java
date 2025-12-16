package battleship.model;

import lombok.Getter;

/**
 * Enum representing the possible orientations in the Battleship game.
 * Each orientation is associated with a specific angle in radians.
 *
 * The orientations are:
 * - HORIZONTAL_RIGHT: Rightwards horizontal direction (0 radians).
 * - HORIZONTAL_LEFT: Leftwards horizontal direction (π radians).
 * - VERTICAL_UP: Upwards vertical direction (π/2 radians).
 * - VERTICAL_DOWN: Downwards vertical direction (-π/2 radians).
 */
public enum Orientation {
    HORIZONTAL_RIGHT(0.0),
    HORIZONTAL_LEFT(Math.PI),
    VERTICAL_UP(Math.PI / 2),
    VERTICAL_DOWN(-Math.PI / 2);

    @Getter
    private final double angle;

    Orientation(double angle){
        this.angle = angle;
    }
}
