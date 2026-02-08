package it.units.battleship;

/**
 * Utility class for serializing and deserializing the game grid.
 */
public class GridMapper {

    /**
     * Serializes a 2D array of CellState into a string representation.
     *
     * @param grid the 2D array of CellState to serialize
     * @return a string representation of the grid
     */
    public static String serialize(CellState[][] grid) {
        if (grid == null) return "";
        StringBuilder flattenedGrid = new StringBuilder();
        for (CellState[] row : grid) {
            for (CellState cell : row) {
                flattenedGrid.append(cell.representation);
            }
        }
        return flattenedGrid.toString();
    }

    /**
     * Deserializes a string representation of the grid into a 2D array of CellState.
     *
     * @param gridSerialized the string representation of the grid
     * @param rows the number of rows in the grid
     * @param cols the number of columns in the grid
     * @return a 2D array of CellState representing the grid
     */
    public static CellState[][] deserialize(String gridSerialized, int rows, int cols) {
        CellState[][] grid = new CellState[rows][cols];
        int index = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (index < gridSerialized.length()) {
                    char code = gridSerialized.charAt(index);
                    grid[r][c] = decodeCharToState(code);
                } else {
                    grid[r][c] = CellState.EMPTY;
                }
                index++;
            }
        }
        return grid;
    }

    private static CellState decodeCharToState(char code) {
        for (CellState state : CellState.values()) {
            if (state.representation == code) {
                return state;
            }
        }
        return CellState.EMPTY;
    }
}
