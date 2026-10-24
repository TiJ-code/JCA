package tij.jca.logging;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CompositeLoggerTest {
    @Test
    void forwardsMessagesToEveryDelegateInOrder() {
        RecordingLogger first = new RecordingLogger();
        RecordingLogger second = new RecordingLogger();
        CompositeLogger logger = new CompositeLogger(first, second);

        logger.info("User {} logged in.", "alice");

        Assertions.assertEquals(
                List.of("INFO:User {} logged in.:[alice]"),
                first.entries
        );
        Assertions.assertEquals(
                List.of("INFO:User {} logged in.:[alice]"),
                second.entries
        );
    }

    @Test
    void forwardsThrowablesToEveryDelegate() {
        RecordingLogger first = new RecordingLogger();
        RecordingLogger second = new RecordingLogger();
        CompositeLogger logger = new CompositeLogger(first, second);
        IllegalStateException failure =
                new IllegalStateException("simulated failure");

        logger.error(failure, "Message delivery failed.");

        Assertions.assertEquals(1, first.throwables.size());
        Assertions.assertEquals(failure, first.throwables.getFirst());
        Assertions.assertEquals(1, second.throwables.size());
        Assertions.assertEquals(failure, second.throwables.getFirst());
    }

    @Test
    void forwardsPrefixAndPathToEveryDelegate() {
        RecordingLogger first = new RecordingLogger();
        RecordingLogger second = new RecordingLogger();
        CompositeLogger logger = new CompositeLogger(first, second);
        Path path = Path.of("logs", "jca.log");

        logger.setClassPrefix("tij.jca.application.UserService");
        logger.setPath(path);

        Assertions.assertEquals("tij.jca.application.UserService", first.prefix);
        Assertions.assertEquals("tij.jca.application.UserService", second.prefix);
        Assertions.assertEquals(path, first.path);
        Assertions.assertEquals(path, second.path);
    }

    @Test
    void contextualLoggerWrapsEveryDelegate() {
        RecordingLogger first = new RecordingLogger();
        RecordingLogger second = new RecordingLogger();
        CompositeLogger logger = new CompositeLogger(first, second);

        logger.with("requestId", "r-1")
                .with(Map.of("userId", "u-1"))
                .info("Request completed.");

        Assertions.assertEquals(
                Map.of("requestId", "r-1", "userId", "u-1"),
                first.context
        );
        Assertions.assertEquals(
                Map.of("requestId", "r-1", "userId", "u-1"),
                second.context
        );
    }

    @Test
    void isEnabledWhenAnyDelegateIsEnabled() {
        RecordingLogger disabled = new RecordingLogger();
        disabled.enabled = false;

        RecordingLogger enabled = new RecordingLogger();
        enabled.enabled = true;

        CompositeLogger logger = new CompositeLogger(disabled, enabled);

        Assertions.assertTrue(logger.isEnabled(LogLevel.INFO));
    }

    @Test
    void isDisabledWhenEveryDelegateIsDisabled() {
        RecordingLogger first = new RecordingLogger();
        RecordingLogger second = new RecordingLogger();
        first.enabled = false;
        second.enabled = false;

        CompositeLogger logger = new CompositeLogger(first, second);

        Assertions.assertFalse(logger.isEnabled(LogLevel.INFO));
    }

    @Test
    void rejectsAnEmptyDelegateList() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new CompositeLogger()
        );
    }

    @Test
    void rejectsNullDelegates() {
        Assertions.assertThrows(
                NullPointerException.class,
                () -> new CompositeLogger((ILogger) null)
        );
    }

    private static final class RecordingLogger implements ILogger {

        private final List<String> entries = new ArrayList<>();
        private final List<Throwable> throwables = new ArrayList<>();
        private Map<String, ?> context = Map.of();
        private String prefix;
        private Path path;
        private boolean enabled = true;

        @Override
        public void trace(String message, Object... args) {
            log(LogLevel.TRACE, message, args);
        }

        @Override
        public void debug(String message, Object... args) {
            log(LogLevel.DEBUG, message, args);
        }

        @Override
        public void info(String message, Object... args) {
            log(LogLevel.INFO, message, args);
        }

        @Override
        public void warn(String message, Object... args) {
            log(LogLevel.WARN, message, args);
        }

        @Override
        public void error(String message, Object... args) {
            log(LogLevel.ERROR, message, args);
        }

        @Override
        public void fatal(String message, Object... args) {
            log(LogLevel.FATAL, message, args);
        }

        @Override
        public void log(LogLevel level, String message, Object... args) {
            entries.add(level + ":" + message + ":" + List.of(args));
        }

        @Override
        public void log(
                LogLevel level,
                Throwable throwable,
                String message,
                Object... args
        ) {
            throwables.add(throwable);
            entries.add(level + ":" + message + ":" + List.of(args));
        }

        @Override
        public void forceLog(String message, Object... args) {
            entries.add("FORCED:" + message + ":" + List.of(args));
        }

        @Override
        public ILogger with(String key, Object value) {
            context = new java.util.LinkedHashMap<>(context);
            ((java.util.Map<String, Object>) context).put(key, value);
            return this;
        }

        @Override
        public ILogger with(Map<String, ?> additionalContext) {
            context = new java.util.LinkedHashMap<>(context);
            ((java.util.Map<String, Object>) context).putAll(
                    additionalContext
            );
            return this;
        }

        @Override
        public void setClassPrefix(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public void setPath(Path path) {
            this.path = path;
        }

        @Override
        public boolean isEnabled(LogLevel level) {
            return enabled;
        }
    }
}