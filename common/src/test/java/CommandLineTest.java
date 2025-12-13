import it.units.battleship.service.CommandLineService;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLineTest {

    @Test
    public void testCommandLineHelp() throws ParseException {
        String[] args = {"--help"};
        assertDoesNotThrow(() -> {
            new CommandLineService(args);
        });
        CommandLineService commandLineService = new CommandLineService(args);
        assertTrue(commandLineService.isHelpRequested());
        assertFalse(commandLineService.isVersionRequested());
        assertEquals(443, commandLineService.getServerPort());
    }

    @Test
    public void testCommandLineVersion() throws ParseException {
        String[] args = {"--version"};
        assertDoesNotThrow(() -> {
            new CommandLineService(args);
        });
        CommandLineService commandLineService = new CommandLineService(args);
        assertFalse(commandLineService.isHelpRequested());
        assertTrue(commandLineService.isVersionRequested());
        assertEquals(443, commandLineService.getServerPort());
    }

    @Test
    public void testCommandLinePort() throws ParseException {
        String[] args = {"--port", "8080"};
        assertDoesNotThrow(() -> {
            new CommandLineService(args);
        });
        CommandLineService commandLineService = new CommandLineService(args);
        assertFalse(commandLineService.isHelpRequested());
        assertFalse(commandLineService.isVersionRequested());
        assertEquals(8080, commandLineService.getServerPort());
    }
}
