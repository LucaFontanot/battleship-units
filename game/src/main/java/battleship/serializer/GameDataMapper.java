package battleship.serializer;

import battleship.model.game.Grid;
import battleship.model.game.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.GridMapper;
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
     * @param dtos The list of DTOs received from the network. Must not be null.
     * @return A list of reconstructed {@link Ship} objects.
     * @throws NullPointerException if dtos is null.
     */
    public static List<Ship> toShipList(@NonNull List<ShipDTO> dtos){
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
        String gridSerialized = GridMapper.serialize(grid.getGrid());

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

    /**
     * Extracts the coordinate from a ShotRequestDTO.
     *
     * @param dto The ShotRequestDTO.
     * @return The coordinate.
     */
    public static Coordinate toCoordinate(@NonNull ShotRequestDTO dto) {
        return dto.coord();
    }

    /**
     * Extracts the GameState from a GameStatusDTO.
     *
     * @param dto The GameStatusDTO.
     * @return The GameState.
     */
    public static GameState toGameState(@NonNull GameStatusDTO dto) {
        return dto.state();
    }

    /**
     * Extracts the message from a GameStatusDTO.
     *
     * @param dto The GameStatusDTO.
     * @return The message.
     */
    public static String toMessage(GameStatusDTO dto) {
        return dto == null ? null : dto.message();
    }

    /**
     * Extracts the fleet configuration rules from a GameConfigDTO.
     *
     * @param dto The GameConfigDTO.
     * @return A map of ship types and their required counts.
     */
    public static Map<ShipType, Integer> toFleetRules(@NonNull GameConfigDTO dto) {
        return dto.fleetRules();
    }

    /**
     * Extracts the number of rows from a GameConfigDTO.
     *
     * @param dto The GameConfigDTO.
     * @return The number of rows.
     */
    public static int toRows(@NonNull GameConfigDTO dto) {
        return dto.rows();
    }

    /**
     * Extracts the number of columns from a GameConfigDTO.
     *
     * @param dto The GameConfigDTO.
     * @return The number of columns.
     */
    public static int toCols(@NonNull GameConfigDTO dto) {
        return dto.cols();
    }

    /**
     * Extracts the serialized grid string from a GridUpdateDTO.
     *
     * @param dto The GridUpdateDTO.
     * @return The serialized grid.
     */
    public static String toGridSerialized(@NonNull GridUpdateDTO dto) {
        return dto.gridSerialized();
    }

    /**
     * Converts the list of ShipDTOs inside a GridUpdateDTO into a list of Ship domain objects.
     *
     * @param dto The GridUpdateDTO.
     * @return A list of {@link Ship} objects.
     */
    public static List<Ship> toShipList(@NonNull GridUpdateDTO dto) {
        return toShipList(dto.fleet());
    }

    /**
     * Extracts the shot outcome from a GridUpdateDTO.
     *
     * @param dto The GridUpdateDTO.
     * @return True if the shot was a hit, false otherwise.
     */
    public static boolean toShotOutcome(@NonNull GridUpdateDTO dto) {
        return dto.shotOutcome();
    }
}
