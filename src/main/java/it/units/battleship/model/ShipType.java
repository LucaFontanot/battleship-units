package it.units.battleship.model;

import lombok.Getter;

import java.awt.*;
import java.util.List;

/**
 * Represents various types of ships used in the Battleship game.
 * Each ship type is defined by its size, name, and a predefined frame of coordinates relative to its structure.
 *
 * Each ship type has the following characteristics:
 * - A numerical size indicating the number of cells occupied by the ship on the grid.
 * - A name used to identify the ship type.
 * - A predefined layout represented as a list of relative coordinates.
 *
 * The size is used to determine the space the ship occupies on the grid.
 * The predefined frame defines the shape or structure of the ship.
 */
public enum ShipType {
    CARRIER(7, "Carrier", List.of(new Coordinate(0, 0), new Coordinate(0, 1), new Coordinate(0, 2), new Coordinate(0,3), new Coordinate(1,0), new Coordinate(1,1), new Coordinate(1,2))),
    BATTLESHIP(5, "Battleship", List.of(new Coordinate(0,0), new Coordinate(0,1), new Coordinate(0,2), new Coordinate(0,3), new Coordinate(0,4))),
    CRUISER(4, "Cruiser", List.of(new Coordinate(0,0), new Coordinate(0,1), new Coordinate(0,2), new Coordinate(0,3))),
    FRIGATE(3, "Frigate", List.of(new Coordinate(0,0), new Coordinate(0,1), new Coordinate(0,2))),
    DESTROYER(2, "Destroyer", List.of(new Coordinate(0,0), new Coordinate(0,1))),;

    @Getter
    private final int size;
    @Getter
    private final String name;
    @Getter
    private final List<Coordinate> shipFrame;

    ShipType(int size, String name, List<Coordinate> shipFrame) {
        this.size = size;
        this.name = name;
        this.shipFrame = shipFrame;
    }
}
