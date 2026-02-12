package it.units.battleship.view.utils;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.jthemedetecor.OsThemeDetector;

/**
 * Utility class for selecting and applying UI themes.
 */
public class ThemeSelector {

    /**
     * Selects and applies the theme based on the operating system's current theme setting.
     */
    public static void selectAutomaticTheme() {
        OsThemeDetector detector = OsThemeDetector.getDetector();
        if (detector.isDark()) {
            selectDarkTheme();
        } else {
            selectLightTheme();
        }
    }

    /**
     * Applies the dark theme.
     */
    public static void selectDarkTheme() {
        FlatDarkLaf.setup();
    }

    /**
     * Applies the light theme.
     */
    public static void selectLightTheme() {
        FlatLightLaf.setup();
    }
}
