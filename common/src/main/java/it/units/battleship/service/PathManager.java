package it.units.battleship.service;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathManager {

    /**
     * Gets the program path for the persistent data: ${user.home}/units/battleship
     * @return the path
     */
    public static Path getProgramPath(){
        Path path = Paths.get(System.getProperty("user.home"));
        Path units = path.resolve("units");
        if (!units.toFile().exists()) {
            units.toFile().mkdir();
        }
        Path app = units.resolve("battleship");
        if (!app.toFile().exists()) {
            app.toFile().mkdir();
        }
        return app;
    }
}
