package it.units.battleship.data.socket.payloads;

import it.units.battleship.Coordinate;

public record ShotRequestDTO(
        Coordinate coord
) {}
