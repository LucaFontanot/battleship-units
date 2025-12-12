package it.units.battleship.model.shipType;

import lombok.Getter;

/**
 * Enumeration representing the types of ships in the Battleship game.
 * Each ship type has a defined size and a name.
 */
public enum ShipType {
    CARRIER(7, "Carrier"),
    BATTLESHIP(6, "Battleship"),
    CRUISER(4, "Cruiser"),
    SUBMARINE(3, "Submarine"),
    DESTROYER(2, "Destroyer");

    @Getter
    private final int size;
    @Getter
    private final String name;

    ShipType(int size, String name) {
        this.size = size;
        this.name = name;
    }
}
