package it.units.battleship.controller.mode.ai;

import it.units.battleship.model.FleetManager;
import it.units.battleship.model.Grid;
import it.units.battleship.model.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;

import java.util.*;

import static it.units.battleship.Defaults.MAX_ATTEMPTS;

/**
 * Implementation of an AI opponent.
 * Search randomly for a ship, then it focuses on sunk the found ship
 */
public class SimpleAIOpponent implements AIOpponent {

    private final Grid grid;
    private final FleetManager fleetManager;
    private final Random random = new Random();

    private final Set<Coordinate> shotsFired = new HashSet<>();

    private List<Ship> opponentFleet = new ArrayList<>();

    public SimpleAIOpponent(Grid grid, FleetManager fleetManager) {
        this.grid = grid;
        this.fleetManager = fleetManager;
    }

    @Override
    public void placeShips() {
        Map<ShipType, Integer> required = fleetManager.getRequiredFleetConfiguration();

        for (Map.Entry<ShipType, Integer> entry : required.entrySet()) {
            ShipType type = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {
                boolean placed = false;
                int attempts = 0;

                while (!placed && attempts < MAX_ATTEMPTS) {
                    int row = random.nextInt(grid.getRow());
                    int col = random.nextInt(grid.getCol());
                    Orientation orientation = random.nextBoolean()
                            ? Orientation.HORIZONTAL_RIGHT
                            : Orientation.VERTICAL_UP;

                    try {
                        Coordinate start = new Coordinate(row, col);
                        Ship ship = Ship.createShip(start, orientation, type, grid);
                        placed = fleetManager.addShip(ship);
                    } catch (IllegalArgumentException e) {
                        // Not valid position, retry
                    }
                    attempts++;
                }
            }
        }
    }

    @Override
    public Coordinate calculateNextShot() {

        // Check if we have hit but not sunk a ship, if so we can try to find the rest of the ship
        for (Ship ship : opponentFleet) {
            if (!ship.isSunk() && !ship.getHitCoordinates().isEmpty()) {
                for (Coordinate hitCoord : ship.getHitCoordinates()) {
                    shotsFired.add(hitCoord);
                    Coordinate target = getRandomAdjacentCoordinate(hitCoord);
                    if (target != null) {
                        shotsFired.add(target);
                        return target;
                    }
                }
            }
        }

        // Otherwise shoot randomly
        Coordinate shot;
        do {
            int row = random.nextInt(grid.getRow());
            int col = random.nextInt(grid.getCol());
            shot = new Coordinate(row, col);
        } while (shotsFired.contains(shot));

        shotsFired.add(shot);
        return shot;
    }

    @Override
    public void processLastShotResult(Grid grid, List<Ship> fleet, boolean shotOutcome) {
        this.opponentFleet = fleet;
    }

    private Coordinate getRandomAdjacentCoordinate(Coordinate coord) {
        List<Coordinate> adjacentCoords = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            Coordinate adjacent = new Coordinate(
                    coord.row() + dir[0],
                    coord.col() + dir[1]
            );

            if (isValidCoordinate(adjacent) && !shotsFired.contains(adjacent)) {
                adjacentCoords.add(adjacent);
            }
        }

        if (adjacentCoords.isEmpty()) {
            return null;
        }

        return adjacentCoords.get(random.nextInt(adjacentCoords.size()));
    }

    private boolean isValidCoordinate(Coordinate coord) {
        return coord.row() >= 0 && coord.row() < grid.getRow()
                && coord.col() >= 0 && coord.col() < grid.getCol();
    }
}