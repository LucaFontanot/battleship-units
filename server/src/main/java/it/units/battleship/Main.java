package it.units.battleship;

import it.units.battleship.service.CommandLineService;
import it.units.battleship.service.PathManager;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

public class Main {

    /**
     * Main method to start the Battleship server application.
     *
     * @param args Command line arguments for configuring the server. Supported options include:
     *             -h, --help: Display help information about the command line options.
     *             -v, --version: Display the version of the Battleship server.
     */
    public static void main(String[] args) {
        System.setProperty("programPath", PathManager.getProgramPath().resolve("logs").toAbsolutePath().normalize().toString());
        try {
            CommandLineService cls = new CommandLineService(args);
            Logger.setDebugEnabled(cls.isDebugEnabled());
            if (cls.isHelpRequested()) {
                cls.printHelp();
            } else if (cls.isVersionRequested()) {
                System.out.println("Battleship Server Version: " + BuildConstants.VERSION);
            } else {
                WebServerApp webServerApp = new WebServerApp(cls.getServerPort());
                Thread serverThread = new Thread(webServerApp);
                serverThread.start();
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}
