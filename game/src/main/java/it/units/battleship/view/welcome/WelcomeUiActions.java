package it.units.battleship.view.welcome;

/**
 * Interface defining actions that can be performed from the welcome UI.
 */
public interface WelcomeUiActions {

    /**
     * Called when the user selects the single-player mode.
     */
    void onSinglePlayerSelected();

    /**
     * Called when the user selects the online multiplayer mode.
     */
    void onOnlineMultiplayerSelected();

    /**
     * Called when the user selects the automatic theme option.
     */
    void onThemeAutoSelected();

    /**
     * Called when the user selects the light theme option.
     */
    void onThemeLightSelected();

    /**
     * Called when the user selects the dark theme option.
     */
    void onThemeDarkSelected();
}
