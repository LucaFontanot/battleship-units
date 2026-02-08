package it.units.battleship.data.socket.payloads;

import it.units.battleship.ShipType;

public record GameConfigDTO (
    int rows,
    int cols,
    java.util.Map<ShipType, Integer> fleetRules
){}
