package it.units.battleship.data.socket.payloads;

import java.util.List;

/**
 * Data Transfer Object for grid updates.
 *
 * @param shotOutcome    True if the shot was a hit, false otherwise.
 *                       This field is included because {@code GridUpdateDTO} is used exclusively
 *                       to update the opponent's grid following a shot request,
 *                       so the outcome of that shot is required.
 * @param gridSerialized The serialized string representation of the grid.
 * @param fleet          The list of ships representing the fleet state.
 */
public record GridUpdateDTO(
        boolean shotOutcome,
        String gridSerialized,
        List<ShipDTO> fleet
) {}
