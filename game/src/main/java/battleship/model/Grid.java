package battleship.model;

import it.units.battleship.Coordinate;
import lombok.Getter;
import lombok.NonNull;

/**
 * Represents a grid for the Battleship game, containing cells that can be in different states (EMPTY, HIT, or MISS).
 * The grid is initialized with a specific number of rows and columns, and a navy (list of ships) is placed on it.
 * Each cell's state can be individually accessed and modified through coordinate-based operations.
 *
 * The grid tracks the state of all ships and can determine if the game has been won by checking if all ships have been hit.
 *
 * @see CellStates for possible cell states
 * @see Coordinate for coordinate system
 */

public class Grid {

    @Getter
    private final int row;
    @Getter
    private final int col;
    
    private final CellStates[][] grid;

    public Grid(int row, int col){
        if (row<=1 || col<=1){
            throw new IllegalArgumentException("Columns and rows value must be strictly positive");
        }
        this.row = row;
        this.col = col;

        this.grid = new CellStates[row][col];

        this.initializeGrid();
    }

    /**
     * Initializes the grid by setting each cell to the default state of CellStates.EMPTY.
     */
    private void initializeGrid(){
        for (int i=0; i < row; i++){
            for (int j=0; j < col; j++){
                grid[i][j] = CellStates.EMPTY;
            }
        }
    }

    /**
     * Updates the state of the cell at the specified coordinate in the grid.
     *
     * @param coordinate the coordinate of the cell to update; must be non-null and within the grid's bounds
     * @param newState the new state to assign to the specified cell; must be non-null
     * @throws IndexOutOfBoundsException if the specified coordinate is outside the grid's dimensions
     */
    public void changeState(@NonNull Coordinate coordinate, @NonNull CellStates newState){
        if (coordinate.row() < 0 || coordinate.row() >= row || coordinate.col() < 0 || coordinate.col() >= col) {
            throw new IndexOutOfBoundsException("Coordinates must respect grid dimension");
        }

        grid[coordinate.row()][coordinate.col()] = newState;
    }

    /**
     * Retrieves the state of the cell at the given coordinate in the grid.
     *
     * @param coordinate the coordinate of the cell to retrieve the state from; must be within the grid's bounds
     * @return the state of the cell at the specified coordinate
     * @throws IndexOutOfBoundsException if the specified coordinate is outside the grid's dimensions
     */
    public CellStates getState(@NonNull Coordinate coordinate){
        if (coordinate.row() < 0 || coordinate.row() >= row || coordinate.col() < 0 || coordinate.col() >= col) {
            throw new IndexOutOfBoundsException("Coordinates must respect grid dimension");
        }

        return grid[coordinate.row()][coordinate.col()];
    }

    /**
     * Serializes the current state of the grid into a string representation, where each cell
     * is represented by a specific character based on its state:
     * - '0' for CellStates.EMPTY
     * - 'X' for CellStates.HIT
     * - 'M' for CellStates.MISS
     *
     * @return a string representation of the grid's current state
     */
    public String gridSerialization(){
        StringBuilder flattedGrid = new StringBuilder();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                flattedGrid.append(grid[i][j].representation);
            }
        }
        return flattedGrid.toString();
    }
}
