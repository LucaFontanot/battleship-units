package battleship.controller.mode.ai;

import battleship.model.game.FleetManager;
import battleship.model.game.Grid;
import battleship.model.game.Ship;
import it.units.battleship.Coordinate;
import it.units.battleship.Orientation;
import it.units.battleship.ShipType;

import java.util.*;

/**
 * Implementation of an AI opponent.
 * Search randomly for a ship, then it focuses on sunk the found ship
 */
public class SimpleAIOpponent implements AIOpponent {

    private final Grid grid;
    private final FleetManager fleetManager;
    private final Random random = new Random();

    private final Set<Coordinate> shotsFired = new HashSet<>();
    private final Queue<Coordinate> targetQueue = new LinkedList<>();
    private Coordinate lastHit = null;

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

                while (!placed && attempts < 100) {
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
        // If we have target in queue we can use those
        while (!targetQueue.isEmpty()) {
            Coordinate target = targetQueue.poll();
            if (!shotsFired.contains(target) && isValidCoordinate(target)) {
                shotsFired.add(target);
                return target;
            }
        }

        // Otherwise shoot randomly
        Coordinate shot;
        do {
            int row = random.nextInt(10);
            int col = random.nextInt(10);
            shot = new Coordinate(row, col);
        } while (shotsFired.contains(shot));

        shotsFired.add(shot);
        return shot;
    }

    @Override
    public void processLastShotResult(boolean hit) {
        if (hit && lastHit != null) {
            // Add neighborhood cells added in the queue
            addAdjacentTargets(lastHit);
        }
    }

    public void setLastShot(Coordinate coord, boolean hit) {
        if (hit) {
            lastHit = coord;
            addAdjacentTargets(coord);
        }
    }

    private void addAdjacentTargets(Coordinate coord) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            Coordinate adjacent = new Coordinate(
                    coord.row() + dir[0],
                    coord.col() + dir[1]
            );

            if (isValidCoordinate(adjacent) && !shotsFired.contains(adjacent)) {
                targetQueue.add(adjacent);
            }
        }
    }

    private boolean isValidCoordinate(Coordinate coord) {
        return coord.row() >= 0 && coord.row() < 10
                && coord.col() >= 0 && coord.col() < 10;
    }
}