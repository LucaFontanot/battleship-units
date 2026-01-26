package it.units.battleship.data.socket.payloads;

import java.util.List;

public record GridUpdateDTO(
        String gridSerialized,
        List<ShipDTO> fleet
) {}
