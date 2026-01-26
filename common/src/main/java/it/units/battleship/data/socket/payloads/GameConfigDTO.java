package it.units.battleship.data.socket.payloads;

public record GameConfigDTO (
    int rows,
    int cols,
    java.util.Map<ShipDTO, Integer> fleetRules
){}
