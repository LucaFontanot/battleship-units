package it.units.battleship.model;

/**
 * Represents a ship in the Battleship game.
 * A ship is composed of multiple cells, each located at a specific coordinate on the grid.
 * The ship can be hit, keep track of its hit count, and determine if it is completely sunk.
 */
public interface IShip {
    boolean addHit(Coordinate coordinate);
    boolean isSunk();
    int getSize();
    int getHitsCount();
    ShipType getShipType();
    String getName();
}
