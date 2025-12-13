import it.units.battleship.Logger;
import org.junit.jupiter.api.Test;

public class LoggerTest {

    @Test
    public void testLogging() {
        Logger.log("This is a log message.");
    }

    @Test
    public void testErrorLogging() {
        Logger.error("This is an error message.");
    }

    @Test
    public void testWarnLogging() {
        Logger.warn("This is a warning message.");
    }

    @Test
    public void testDebugLogging() {
        Logger.setDebugEnabled(true);
        Logger.debug("This is a debug message.");
    }

    @Test
    public void testExceptionLogging() {
        try {
            throw new Exception("Test exception");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }
}
