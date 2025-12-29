package it.units.battleship;

public enum GameState {
    SETUP("SETUP"),
    WAITING("WAITING"),
    MY_TURN("MY_TURN"),
    OPPONENT_TURN("OPPONENT_TURN"),
    GAME_OVER("GAME_OVER");

    private final String gameState;

    GameState(String gameState){
        this.gameState = gameState;
    }
}
