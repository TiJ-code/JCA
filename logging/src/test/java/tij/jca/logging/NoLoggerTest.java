package tij.jca.logging;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

class NoLoggerTest {

    @Test
    void discardsEveryLoggingOperation() {
        NoLogger logger = new NoLogger();
        IllegalStateException failure =
                new IllegalStateException("simulated failure");

        logger.trace("Trace {}", 1);
        logger.debug("Debug {}", 2);
        logger.info("Info {}", 3);
        logger.warn("Warn {}", 4);
        logger.error("Error {}", 5);
        logger.fatal("Fatal {}", 6);
        logger.log(LogLevel.INFO, "Log {}", 7);
        logger.log(LogLevel.ERROR, failure, "Failed {}", 8);
        logger.forceLog("Forced {}", 9);
        logger.setClassPrefix("test.logger");
        logger.setPath(Path.of("logs", "jca.log"));
    }

    @Test
    void returnsItselfForContextAndNeverEnablesALevel() {
        NoLogger logger = new NoLogger();

        Assertions.assertSame(logger, logger.with("requestId", "r-1"));
        Assertions.assertSame(
                logger,
                logger.with(Map.of("requestId", "r-1"))
        );

        for (LogLevel level : LogLevel.values()) {
            Assertions.assertFalse(logger.isEnabled(level));
        }
    }
}
