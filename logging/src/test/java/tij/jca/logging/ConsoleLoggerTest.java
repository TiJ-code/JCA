package tij.jca.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
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

    @Test
    void includesStackTraceWhenConfigured() {
        List<String> output = new ArrayList<>();
        ConsoleLogger logger = new ConsoleLogger(output::add);

        logger.error(
                new IllegalStateException("simulated failure"),
                "Operation failed."
        );

        Assertions.assertEquals(1, output.size());
        Assertions.assertTrue(output.getFirst().contains("simulated failure"));
        Assertions.assertTrue(output.getFirst().contains("ConsoleLoggerTest"));
    }

    @Test
    void forceLogBypassesTheMinimumLevel() {
        List<String> output = new ArrayList<>();
        ConsoleLogger logger = new ConsoleLogger(output::add);

        LoggerFactory.setConfig(
                DefaultLogConfig.builder()
                        .minimumLogLevel(LogLevel.FATAL)
                        .build()
        );

        logger.info("Hidden");
        logger.forceLog("Always emitted");

        Assertions.assertEquals(1, output.size());
        Assertions.assertTrue(output.getFirst().contains("Always emitted"));
    }

    @Test
    void formatsPlaceholdersAndAppendsSurplusArguments() {
        List<String> output = new ArrayList<>();
        ConsoleLogger logger = new ConsoleLogger(output::add);

        logger.info("User {} has {} messages.", "alice", 3);
        logger.info("User {}", "alice", "extra");

        Assertions.assertTrue(
                output.get(0).contains("User alice has 3 messages.")
        );
        Assertions.assertTrue(
                output.get(1).contains("User alice extra")
        );
    }

    @Test
    void contextualLoggerDoesNotMutateItsSourceLogger() {
        List<String> output = new ArrayList<>();
        ConsoleLogger logger = new ConsoleLogger(output::add);

        logger.with("requestId", "r-1").info("Contextual entry");
        logger.info("Source entry");

        Assertions.assertTrue(output.get(0).contains("requestId=r-1"));
        Assertions.assertFalse(output.get(1).contains("requestId=r-1"));
    }

    @Test
    void factoryAppliesClassPrefixToConsoleLogger() {
        List<String> output = new ArrayList<>();

        LoggerFactory.setLoggerFactory(() -> new ConsoleLogger(output::add));

        LoggerFactory.getLogger(ConsoleLoggerTest.class).info("Created");

        Assertions.assertEquals(1, output.size());
        Assertions.assertTrue(
                output.getFirst().contains(
                        "[" + ConsoleLoggerTest.class.getName() + "]"
                )
        );
    }

    @Test
    void doesNotForceLogWhenLoggingIsDisabled() {
        List<String> output = new ArrayList<>();
        ConsoleLogger logger = new ConsoleLogger(output::add);

        LoggerFactory.setConfig(
                DefaultLogConfig.builder()
                        .enabled(false)
                        .build()
        );

        logger.forceLog("This must not be emitted.");

        Assertions.assertTrue(output.isEmpty());
    }
}