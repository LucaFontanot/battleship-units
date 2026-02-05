package battleship.controller.handlers;

import battleship.model.FleetManager;
import battleship.model.Grid;
import battleship.model.Ship;
import battleship.model.converter.GameDataMapper;
import it.units.battleship.Coordinate;
import it.units.battleship.GameState;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;
import it.units.battleship.data.socket.GameMessageType;
import it.units.battleship.data.socket.payloads.GameStatusDTO;
import it.units.battleship.data.socket.payloads.GridUpdateDTO;
import it.units.battleship.data.socket.payloads.ShipDTO;
import it.units.battleship.data.socket.payloads.ShotRequestDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class LocalPlayerCommunication extends AbstractPlayerCommunication {

    private static final int MAX_PLACEMENT_ATTEMPTS = 1000;
    private static final Orientation[] ORIENTATIONS = Orientation.values();
    private final Grid computerGrid;
    private final FleetManager computerFleet;
    private final Random random;
    private final int rows;
    private final int cols;
    private final Set<Coordinate> availableShots = new LinkedHashSet<>();
    private final Set<Coordinate> pendingHits = new HashSet<>();
    private Coordinate lastComputerShot;

    public LocalPlayerCommunication(int rows, int cols, Map<ShipType, Integer> fleetConfiguration) {
        this(rows, cols, fleetConfiguration, new Random());
    }

    LocalPlayerCommunication(int rows, int cols, Map<ShipType, Integer> fleetConfiguration, Random random) {
        this.rows = rows;
        this.cols = cols;
        this.random = random;
        this.computerGrid = new Grid(rows, cols);
        this.computerFleet = new FleetManager(computerGrid, fleetConfiguration);
        initializeAvailableShots();
        placeRandomFleet(fleetConfiguration);
    }

    @Override
    public <T> void sendMessage(GameMessageType type, T payload) {
        if (type == GameMessageType.SHOT_REQUEST) {
            handlePlayerShot((ShotRequestDTO) payload);
            return;
        }
        if (type == GameMessageType.GRID_UPDATE) {
            handleComputerShotResult((GridUpdateDTO) payload);
        }
    }

    private void handlePlayerShot(ShotRequestDTO shotRequestDTO) {
        boolean shotOutcome = computerFleet.handleIncomingShot(shotRequestDTO.coord());
        GridUpdateDTO updateDTO = GameDataMapper.toGridUpdateDTO(shotOutcome, computerGrid, computerFleet.getFleet());
        onOpponentGridUpdate(updateDTO);

        if (computerFleet.isGameOver()) {
            onGameStatusReceived(new GameStatusDTO(GameState.GAME_OVER, "You win!"));
            return;
        }

        Coordinate computerShot = selectComputerShot();
        if (computerShot != null) {
            lastComputerShot = computerShot;
            onShotReceived(new ShotRequestDTO(computerShot));
        }
    }

    private void handleComputerShotResult(GridUpdateDTO gridUpdateDTO) {
        if (lastComputerShot != null && gridUpdateDTO.shotOutcome()) {
            pendingHits.add(lastComputerShot);
        }

        for (ShipDTO sunkShip : gridUpdateDTO.fleet()) {
            pendingHits.removeAll(sunkShip.coordinates());
        }

        lastComputerShot = null;
    }

    private Coordinate selectComputerShot() {
        if (availableShots.isEmpty()) {
            return null;
        }

        List<Coordinate> candidates = resolveShotCandidates();
        Coordinate selected = candidates.get(random.nextInt(candidates.size()));
        availableShots.remove(selected);
        return selected;
    }

    private List<Coordinate> resolveShotCandidates() {
        if (!pendingHits.isEmpty()) {
            Set<Coordinate> neighbors = new HashSet<>();
            for (Coordinate hit : pendingHits) {
                addNeighbor(neighbors, hit.row() - 1, hit.col());
                addNeighbor(neighbors, hit.row() + 1, hit.col());
                addNeighbor(neighbors, hit.row(), hit.col() - 1);
                addNeighbor(neighbors, hit.row(), hit.col() + 1);
            }
            if (!neighbors.isEmpty()) {
                return new ArrayList<>(neighbors);
            }
        }
        return new ArrayList<>(availableShots);
    }

    private void addNeighbor(Set<Coordinate> neighbors, int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return;
        }
        Coordinate candidate = new Coordinate(row, col);
        if (availableShots.contains(candidate)) {
            neighbors.add(candidate);
        }
    }

    private void initializeAvailableShots() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                availableShots.add(new Coordinate(row, col));
            }
        }
    }

    private void placeRandomFleet(Map<ShipType, Integer> fleetConfiguration) {
        for (Map.Entry<ShipType, Integer> entry : fleetConfiguration.entrySet()) {
            int placed = 0;
            int attempts = 0;
            while (placed < entry.getValue()) {
                if (attempts++ > MAX_PLACEMENT_ATTEMPTS) {
                    throw new IllegalStateException(
                            "Unable to place " + entry.getKey() + " (" + placed + "/" + entry.getValue()
                                    + ") on " + rows + "x" + cols
                                    + " grid. Grid may be too small for the fleet configuration."
                    );
                }
                Coordinate coord = new Coordinate(random.nextInt(rows), random.nextInt(cols));
                Orientation orientation = randomOrientation();
                try {
                    Ship ship = Ship.createShip(coord, orientation, entry.getKey(), computerGrid);
                    if (computerFleet.addShip(ship)) {
                        placed++;
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    private Orientation randomOrientation() {
        return ORIENTATIONS[random.nextInt(ORIENTATIONS.length)];
    }
}
