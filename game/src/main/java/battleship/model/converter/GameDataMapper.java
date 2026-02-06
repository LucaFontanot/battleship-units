package battleship.model.converter;

import battleship.model.Grid;
import battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.ShipType;
import it.units.battleship.data.socket.payloads.*;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class responsible for mapping between Domain Entities and Data Transfer Objects (DTOs).
 *
 * This mapper isolates the core game logic (Domain Layer) from the network protocol specifics (DTO Layer).
 * It ensures that internal model changes do not directly break the network contract, and vice-versa,
 * by providing a centralized place for data conversion.
 *
 * Key Responsibilities:
 *  - Converting {@link ShipDTO} received from the network into {@link Ship} domain objects usable by the game logic.
 *  - Converting local {@link Ship} objects into {@link ShipDTO}s ready for network transmission.
 */
public class GameDataMapper{

    /**
     * Converts a list of ShipDTOs into a list of Ship domain objects.
     * Useful when receiving the opponent's fleet configuration or grid updates.
     *
     * @param dtos The list of DTOs received from the network. Can be null.
     * @return A list of reconstructed {@link Ship} objects. Returns an empty list if the input is null.
     */
    public static List<Ship> toShipList(@NonNull List<ShipDTO> dtos){
        if (dtos == null) return List.of();
        return dtos.stream().map(GameDataMapper::toShip).collect(Collectors.toList());
    }

    /**
     * Converts a single ShipDTO into a Ship domain object.
     * Uses {@link Ship#restoreShip} to recreate the ship instance with its exact coordinates and state.
     *
     * @param dto The ShipDTO to convert.
     * @return A {@link Ship} instance with the properties defined in the DTO.
     */
    public static Ship toShip (@NonNull ShipDTO dto){
        return Ship.restoreShip(new java.util.LinkedHashSet<>(dto.coordinates()),
                                dto.type(),
                                dto.orientation());
    }

    /**
     * Converts a list of Ship domain objects into ShipDTOs.
     * Typically used before sending the local fleet status to the opponent via a grid update message.
     *
     * @param fleet The list of local {@link Ship} objects.
     * @return A list of {@link ShipDTO} ready for serialization.
     */
    public static List<ShipDTO> toShipDTO(@NonNull List<Ship> fleet){
        return fleet.stream()
                .map(s -> new ShipDTO(s.getShipType(),
                                            s.getCoordinates(),
                                            s.getOrientation()))
                .collect(Collectors.toList());
    }
    /**
     * Creates a GridUpdateDTO from the current game state after processing a shot.
     *
     * @param shotOutcome    True if the shot was a hit, false otherwise.
     * @param grid           The grid to serialize.
     * @param fleet          The complete fleet to extract sunk ships from.
     * @return A GridUpdateDTO ready for transmission.
     */
    public static GridUpdateDTO toGridUpdateDTO(boolean shotOutcome,@NonNull Grid grid,@NonNull List<Ship> fleet) {
        List<Ship> sunkShips = fleet.stream()
                .filter(Ship::isSunk)
                .toList();
        List<ShipDTO> sunkShipsDTO = toShipDTO(sunkShips);
        String gridSerialized = grid.gridSerialization();

        return new GridUpdateDTO(shotOutcome, gridSerialized, sunkShipsDTO);
    }

    public static GameConfigDTO toGameConfigDTO(int rows, int cols,@NonNull Map<ShipType, Integer> fleetConfiguration){
        return new GameConfigDTO(rows, cols, fleetConfiguration);
    }

    public static GameStatusDTO toGameStatusDTO(@NonNull GameState gameState, String message){
        return new GameStatusDTO(gameState, message);
    }

    public static ShotRequestDTO toShotRequestDTO(@NonNull Coordinate coordinate){
        return  new ShotRequestDTO(coordinate);
    }
}
