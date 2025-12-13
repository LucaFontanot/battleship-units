package it.units.battleship.service;

import org.apache.commons.cli.*;

/**
 * Service for parsing and handling command-line arguments.
 */
public class CommandLineService {
    final Options options = new Options();
    final CommandLineParser parser = new DefaultParser();
    final CommandLine cmd;

    /**
     * Constructs a CommandLineService and parses the provided arguments.
     * @param args the command-line arguments
     * @throws ParseException if there is an error parsing the arguments
     */
    public CommandLineService(String[] args) throws ParseException {
        buildOptions();
        cmd = parser.parse(options, args);
    }

    /**
     * Builds the command-line options.
     */
    void buildOptions() {
        options.addOption("h", "help", false, "Show help");
        options.addOption("s", "server", false, "Start application as webserver");
        options.addOption("p", "port", true, "Webserver port number");
        options.addOption("v", "version", false, "Show version");
    }

    /**
     * Checks if help was requested.
     * @return true if help was requested, false otherwise
     */
    public boolean isHelpRequested(){
        return cmd.hasOption("h");
    }

    /**
     * Checks if version information was requested.
     * @return true if version information was requested, false otherwise
     */
    public boolean isVersionRequested() {
        return cmd.hasOption("v");
    }

    /**
     * Checks if the application should run in server mode.
     * @return true if server mode is requested, false otherwise
     */
    public boolean isServerMode() {
        return cmd.hasOption("s");
    }

    /**
     * Gets the server port number.
     * @return the server port number, or 443 if not specified or invalid
     */
    public int getServerPort() {
        if (cmd.hasOption("p")) {
            try {
                return Integer.parseInt(cmd.getOptionValue("p"));
            } catch (NumberFormatException e) {
                return 443;
            }
        } else {
            return 443;
        }
    }
}
