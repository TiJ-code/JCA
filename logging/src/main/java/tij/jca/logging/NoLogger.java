package tij.jca.logging;

/**
 * No-operation implementation of {@link ILogger}.
 *
 * <p>This logger silently discards all log entries and performs no output.
 * It is used as the default logging implementation when no concrete logger
 * implementation has been configured.</p>
 *
 * <p>The implementation is intentionally stateless. Context and configuration
 * operations therefore return this instance or have no effect.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class NoLogger implements ILogger {

    /**
     * Creates a no-operation logger.
     */
    public NoLogger() {
    }

    /**
     * Discards a trace-level log entry.
     *
     * @param message ignored message template
     * @param args ignored formatting arguments
     */
    @Override
    public void trace(String message, Object... args) {
    }

    /**
     * Discards a debug-level log entry.
     *
     * @param message ignored message template
     * @param args ignored formatting arguments
     */
    @Override
    public void debug(String message, Object... args) {
    }

    /**
     * Discards an info-level log entry.
     *
     * @param message ignored message template
     * @param args ignored formatting arguments
     */
    @Override
    public void info(String message, Object... args) {
    }

    /**
     * Discards a warning-level log entry.
     *
     * @param message ignored message template
     * @param args ignored formatting arguments
     */
    @Override
    public void warn(String message, Object... args) {
    }

    /**
     * Discards an error-level log entry.
     *
     * @param message ignored message template
     * @param args ignored formatting arguments
     */
    @Override
    public void error(String message, Object... args) {
    }

    /**
     * Discards a fatal-level log entry.
     *
     * @param message ignored message template
     * @param args ignored formatting arguments
     */
    @Override
    public void fatal(String message, Object... args) {
    }

    /**
     * Discards a log entry at the specified level.
     *
     * @param level ignored log level
     * @param message ignored message template
     * @param args ignored formatting arguments
     */
    @Override
    public void log(LogLevel level, String message, Object... args) {
    }
    /**
     * Discards a log entry containing an exception.
     *
     * @param level ignored log level
     * @param throwable ignored exception
     * @param message ignored message template
     * @param args ignored formatting arguments
     */
    @Override
    public void log(LogLevel level, Throwable throwable, String message, Object... args) {
    }

    /**
     * Discards a log entry regardless of the configured log level.
     *
     * @param message ignored message template
     * @param args ignored formatting arguments
     */
    @Override
    public void forceLog(String message, Object... args) {
    }

    /**
     * Returns this logger because contextual information is not stored.
     *
     * @param key ignored a context key
     * @param value ignored context value
     * @return this logger
     */
    @Override
    public ILogger with(String key, Object value) {
        return this;
    }

    /**
     * Returns this logger because contextual information is not stored.
     *
     * @param context ignored context
     * @return this logger
     */
    @Override
    public ILogger with(java.util.Map<String, ?> context) {
        return this;
    }

    /**
     * Does nothing because this implementation has no source prefix.
     *
     * @param prefix ignored source prefix
     */
    @Override
    public void setClassPrefix(String prefix) {
    }

    /**
     * Does nothing because this implementation produces no output.
     *
     * @param path ignored an output path
     */
    @Override
    public void setPath(java.nio.file.Path path) {
    }

    /**
     * Always returns {@code false} because this implementation never emits
     * log entries.
     *
     * @param level ignored log level
     * @return always {@code false}
     */
    @Override
    public boolean isEnabled(LogLevel level) {
        return false;
    }
}