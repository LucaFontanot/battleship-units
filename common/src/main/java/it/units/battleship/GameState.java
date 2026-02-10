package it.units.battleship;

public enum GameState {
    SETUP("Setup"),
    WAITING_SETUP("WAITING_SETUP"),
    ACTIVE_TURN("Your turn"),
    WAITING_FOR_OPPONENT("Waiting for opponent"),
    GAME_OVER("Game over");

    private final String gameState;

    GameState(String gameState){
        this.gameState = gameState;
    }
}
