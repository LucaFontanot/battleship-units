package it.units.battleship;

import it.units.battleship.service.CommandLineService;
import org.apache.commons.cli.ParseException;

public class Main {
    public static void main(String[] args) {
        try {
            CommandLineService cls = new CommandLineService(args);
            if (cls.isHelpRequested()){
                //
            } else if (cls.isVersionRequested()){
               //
            } else{
                WebServerApp webServerApp = new WebServerApp(cls.getServerPort());
                Thread serverThread = new Thread(webServerApp);
                serverThread.start();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
