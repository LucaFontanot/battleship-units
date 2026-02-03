package it.units.battleship;

public enum GameState {
    SETUP("SETUP"),
    WAITING("WAITING"),
    ACTIVE_TURN("ACTIVE_TURN"),
    WAITING_FOR_OPPONENT("WAITING_FOR_OPPONENT"),
    GAME_OVER("GAME_OVER");

    private final String gameState;

    GameState(String gameState){
        this.gameState = gameState;
    }
}
