package it.units.battleship;

import it.units.battleship.service.CommandLineService;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            CommandLineService cls = new CommandLineService(args);
            if (cls.isHelpRequested()){
                cls.printHelp();
            } else if (cls.isVersionRequested()){
               System.out.println("Battleship Server Version: " + BuildConstants.VERSION);
            } else{
                WebServerApp webServerApp = new WebServerApp(cls.getServerPort());
                Thread serverThread = new Thread(webServerApp);
                serverThread.start();
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}
