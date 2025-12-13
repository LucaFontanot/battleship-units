package it.units.battleship;

import it.units.battleship.service.PathManager;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Logger utility class for logging messages at different levels.
 */
public class Logger {
    private static final org.apache.logging.log4j.Logger logger;
    @Setter
    private static boolean debugEnabled = false;

    static {
        System.setProperty("programPath", PathManager.getProgramPath().resolve("logs").toAbsolutePath().normalize().toString());
        logger = LogManager.getLogger(Logger.class);
    }

    /**
     * Logs a message at info level.
     * @param message the message to log
     */
    public static void log(String message) {
        logger.info("[LOG] {}", message);
    }

    /**
     * Logs a message at error level.
     * @param message the message to log
     */
    public static void error(String message) {
        logger.error("[ERROR] {}", message);
    }

    /**
     * Logs a message at warn level.
     * @param message the message to log
     */
    public static void warn(String message) {
        logger.warn("[WARN] {}", message);
    }

    /**
     * Logs a message at debug level if debug is enabled.
     * @param message the message to log
     */
    public static void debug(String message) {
        if (debugEnabled) {
            logger.debug("[DEBUG] {}", message);
        }
    }

    /**
     * Logs an exception's stack trace at error level.
     * @param e the exception to log
     */
    public static synchronized void exception(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        logger.error("[EX] [{}] {}", Thread.currentThread().getName(), sw.toString());
    }
}
