package tij.jca.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

class LoggerFactoryTest {

    @AfterEach
    void resetFactory() {
        LoggerFactory.setConfig(DefaultLogConfig.builder().build());
        LoggerFactory.setLoggerFactory(ConsoleLogger::new);
    }

    @Test
    void configuresFactoryLoggerWithNameAndOutputPath() {
        Path outputPath = Path.of("logs", "jca.log");
        RecordingLogger recordingLogger = new RecordingLogger();

        LoggerFactory.setConfig(
                DefaultLogConfig.builder()
                        .outputPath(outputPath)
                        .build()
        );
        LoggerFactory.setLoggerFactory(() -> recordingLogger);

        ILogger logger = LoggerFactory.getLogger("test.logger");

        Assertions.assertSame(recordingLogger, logger);
        Assertions.assertEquals("test.logger", recordingLogger.classPrefix);
        Assertions.assertEquals(outputPath, recordingLogger.outputPath);
    }

    private static final class RecordingLogger implements ILogger {

        private String classPrefix;
        private Path outputPath;

        @Override
        public void trace(String message, Object... args) {
        }

        @Override
        public void debug(String message, Object... args) {
        }

        @Override
        public void info(String message, Object... args) {
        }

        @Override
        public void warn(String message, Object... args) {
        }

        @Override
        public void error(String message, Object... args) {
        }

        @Override
        public void fatal(String message, Object... args) {
        }

        @Override
        public void log(LogLevel level, String message, Object... args) {
        }

        @Override
        public void forceLog(String message, Object... args) {
        }

        @Override
        public void log(
                LogLevel level,
                Throwable throwable,
                String message,
                Object... args
        ) {
        }

        @Override
        public ILogger with(String key, Object value) {
            return this;
        }

        @Override
        public ILogger with(Map<String, ?> context) {
            return this;
        }

        @Override
        public void setClassPrefix(String prefix) {
            classPrefix = prefix;
        }

        @Override
        public void setPath(Path path) {
            outputPath = path;
        }

        @Override
        public boolean isEnabled(LogLevel level) {
            return false;
        }
    }
}
