package battleship.model;

public enum CellState {
    EMPTY('0'),
    HIT('X'),
    SUNK('K'),
    MISS('M');

    public final char representation;

    CellState(char representation){
        this.representation = representation;
    }
}
