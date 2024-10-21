package dev.jamesswafford.chess4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class Log4J2Test {

    private static final Logger LOGGER = LogManager.getLogger(Log4J2Test.class);

    @Test
    public void logTest() {
        LOGGER.debug("Debug message");
        LOGGER.info("Info message");
        LOGGER.warn("Warn message");
        LOGGER.error("Error message");
    }

}
