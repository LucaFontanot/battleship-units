package it.units.battleship;

import it.units.battleship.view.utils.ThemeSelector;
import it.units.battleship.view.welcome.WelcomeUi;
import it.units.battleship.service.CommandLineService;
import it.units.battleship.service.PathManager;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.setProperty("programPath", PathManager.getProgramPath().resolve("logs").toAbsolutePath().normalize().toString());
        try {
            CommandLineService cls = new CommandLineService(args);
            if (cls.isHelpRequested()) {
                cls.printHelp();
            } else if (cls.isVersionRequested()) {
                System.out.println("Battleship Server Version: " + BuildConstants.VERSION);
            } else {
                Logger.setDebugEnabled(true);
                ThemeSelector.selectAutomaticTheme();
                new WelcomeUi().show();
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}