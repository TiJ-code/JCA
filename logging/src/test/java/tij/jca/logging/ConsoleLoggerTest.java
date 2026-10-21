package tij.jca.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ConsoleLoggerTest {

    @AfterEach
    void resetConfiguration() {
        LoggerFactory.setConfig(DefaultLogConfig.builder().build());
        LoggerFactory.setLoggerFactory(ConsoleLogger::new);
    }

    @Test
    void logsMessagesAtOrAboveConfiguredLevel() {
        List<String> output = new ArrayList<>();
        ConsoleLogger logger = new ConsoleLogger(output::add);

        LoggerFactory.setConfig(
                DefaultLogConfig.builder()
                        .minimumLogLevel(LogLevel.INFO)
                        .build()
        );

        logger.debug("Hidden");
        logger.info("Hello {}.", "TiJ");

        Assertions.assertEquals(1, output.size());
        Assertions.assertTrue(output.getFirst().contains("[INFO]"));
        Assertions.assertTrue(output.getFirst().contains("Hello TiJ."));
    }

    @Test
    void doesNotEmitWhenLoggingIsDisabled() {
        List<String> output = new ArrayList<>();
        ConsoleLogger logger = new ConsoleLogger(output::add);

        LoggerFactory.setConfig(
                DefaultLogConfig.builder()
                        .enabled(false)
                        .build()
        );

        logger.error("This must not be emitted.");

        Assertions.assertTrue(output.isEmpty());
    }

    @Test
    void retainsExistingContextWhenAddingContext() {
        List<String> output = new ArrayList<>();
        ConsoleLogger logger = new ConsoleLogger(output::add);

        ILogger requestLogger = logger
                .with("requestId", "r-1")
                .with("userId", "u-1");

        requestLogger.info("Request completed.");

        Assertions.assertEquals(1, output.size());
        Assertions.assertTrue(output.getFirst().contains("requestId=r-1"));
        Assertions.assertTrue(output.getFirst().contains("userId=u-1"));
    }

    @Test
    void retainsExistingContextWhenAddingContextMap() {
        List<String> output = new ArrayList<>();
        ConsoleLogger logger = new ConsoleLogger(output::add);

        ILogger requestLogger = logger
                .with("requestId", "r-1")
                .with(Map.of("userId", "u-1"));

        requestLogger.info("Request completed.");

        Assertions.assertEquals(1, output.size());
        Assertions.assertTrue(output.getFirst().contains("requestId=r-1"));
        Assertions.assertTrue(output.getFirst().contains("userId=u-1"));
    }

    @Test
    void includesStackTraceOnlyWhenConfigured() {
        List<String> output = new ArrayList<>();
        ConsoleLogger logger = new ConsoleLogger(output::add);
        IllegalStateException failure =
                new IllegalStateException("simulated failure");

        LoggerFactory.setConfig(
                DefaultLogConfig.builder()
                        .includeStackTraces(false)
                        .build()
        );

        logger.error(failure, "Operation failed.");

        Assertions.assertEquals(1, output.size());
        Assertions.assertTrue(output.getFirst().contains("simulated failure"));
        Assertions.assertFalse(output.getFirst().contains("ConsoleLoggerTest"));
    }
}