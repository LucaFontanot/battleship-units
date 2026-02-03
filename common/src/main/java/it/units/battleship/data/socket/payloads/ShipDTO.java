package it.units.battleship.data.socket.payloads;

import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;

import java.util.LinkedHashSet;

public record ShipDTO(
        ShipType type,
        LinkedHashSet<Coordinate> coordinates,
        Orientation orientation
        )
{}
