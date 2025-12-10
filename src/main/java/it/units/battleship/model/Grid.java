package it.units.battleship.model;

import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * Represents a grid for the Battleship game, containing cells that can be in different states (EMPTY, HIT, or MISS).
 * The grid is initialized with a specific number of rows and columns, and a navy (list of ships) is placed on it.
 * Each cell's state can be individually accessed and modified through coordinate-based operations.
 *
 * The grid tracks the state of all ships and can determine if the game has been won by checking if all ships have been hit.
 *
 * @see CellStates for possible cell states
 * @see Ship for ship implementation
 * @see Coordinate for coordinate system
 */

public class Grid {

    private final int row;
    private final int col;

    @Getter
    private final CellStates[][] grid;

    @Getter
    private final List<Ship> navy;

    public Grid(int row, int col, List<Ship> navy){
        if (navy.size() == 0){
            throw new IllegalArgumentException("Navy must contain at least one ship.");
        }
        if (row<=0 || col<=0){
            throw new IllegalArgumentException("Columns and rows value must be strictly positive");
        }
        this.row = row;
        this.col = col;

        this.grid = new CellStates[row][col];
        this.navy = navy;

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
}
