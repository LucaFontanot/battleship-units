package battleship.model;

public enum CellStates {
    EMPTY('0'),
    HIT('X'),
    SUNK('K'),
    MISS('M');

    public final char representation;

    CellStates(char representation){
        this.representation = representation;
    }
}
