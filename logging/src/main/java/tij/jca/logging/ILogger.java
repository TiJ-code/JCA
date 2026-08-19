package tij.jca.logging;

import java.nio.file.Path;
import java.util.Map;

/**
 * Provides application logging at different severity levels.
 *
 * <p>A logger is associated with a specific class or source and may optionally
 * contain additional contextual information. Implementations are responsible
 * for formatting and dispatching log entries to their configured output
 * destinations.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface ILogger {

    /**
     * Logs a message at {@link LogLevel#TRACE} level.
     *
     * @param message message template
     * @param args arguments used to format the message
     */
    void trace(String message, Object... args);

    /**
     * Logs a message at {@link LogLevel#DEBUG} level.
     *
     * @param message message template
     * @param args arguments used to format the message
     */
    void debug(String message, Object... args);

    /**
     * Logs a message at {@link LogLevel#INFO} level.
     *
     * @param message message template
     * @param args arguments used to format the message
     */
    void info(String message, Object... args);

    /**
     * Logs a message at {@link LogLevel#WARN} level.
     *
     * @param message message template
     * @param args arguments used to format the message
     */
    void warn(String message, Object... args);

    /**
     * Logs a message at {@link LogLevel#ERROR} level.
     *
     * @param message message template
     * @param args arguments used to format the message
     */
    void error(String message, Object... args);

    /**
     * Logs a message at {@link LogLevel#FATAL} level.
     *
     * @param message message template
     * @param args arguments used to format the message
     */
    void fatal(String message, Object... args);

    /**
     * Logs a message at the specified severity level.
     *
     * @param level severity of the log entry
     * @param message message template
     * @param args arguments used to format the message
     * @throws NullPointerException if {@code level} or {@code message} is {@code null}
     */
    void log(LogLevel level, String message, Object... args);

    /**
     * Logs a message regardless of the configured minimum log level.
     *
     * <p>This method is intended for messages that must always be emitted,
     * such as critical startup or shutdown information.</p>
     *
     * @param message message template
     * @param args arguments used to format the message
     */
    void forceLog(String message, Object... args);

    /**
     * Logs an exception together with a message at the specified severity level.
     *
     * @param level severity of the log entry
     * @param throwable exception associated with the log entry
     * @param message message template
     * @param args arguments used to format the message
     * @throws NullPointerException if {@code level} or {@code message} is {@code null}
     */
    void log(LogLevel level, Throwable throwable, String message, Object... args);

    /**
     * Logs an exception at {@link LogLevel#TRACE} level.
     *
     * @param throwable exception associated with the log entry
     * @param message message template
     * @param args arguments used to format the message
     */
    default void trace(Throwable throwable, String message, Object... args) {
        log(LogLevel.TRACE, throwable, message, args);
    }

    /**
     * Logs an exception at {@link LogLevel#DEBUG} level.
     *
     * @param throwable exception associated with the log entry
     * @param message message template
     * @param args arguments used to format the message
     */
    default void debug(Throwable throwable, String message, Object... args) {
        log(LogLevel.DEBUG, throwable, message, args);
    }

    /**
     * Logs an exception at {@link LogLevel#INFO} level.
     *
     * @param throwable exception associated with the log entry
     * @param message message template
     * @param args arguments used to format the message
     */
    default void info(Throwable throwable, String message, Object... args) {
        log(LogLevel.INFO, throwable, message, args);
    }

    /**
     * Logs an exception at {@link LogLevel#WARN} level.
     *
     * @param throwable exception associated with the log entry
     * @param message message template
     * @param args arguments used to format the message
     */
    default void warn(Throwable throwable, String message, Object... args) {
        log(LogLevel.WARN, throwable, message, args);
    }

    /**
     * Logs an exception at {@link LogLevel#ERROR} level.
     *
     * @param throwable exception associated with the log entry
     * @param message message template
     * @param args arguments used to format the message
     */
    default void error(Throwable throwable, String message, Object... args) {
        log(LogLevel.ERROR, throwable, message, args);
    }

    /**
     * Logs an exception at {@link LogLevel#FATAL} level.
     *
     * @param throwable exception associated with the log entry
     * @param message message template
     * @param args arguments used to format the message
     */
    default void fatal(Throwable throwable, String message, Object... args) {
        log(LogLevel.FATAL, throwable, message, args);
    }

    /**
     * Creates a logger with an additional contextual field.
     *
     * <p>The returned logger should retain the existing logger context and
     * include the supplied key-value pair in subsequent log entries.</p>
     *
     * @param key context key
     * @param value context value
     * @return a logger containing the additional context
     * @throws NullPointerException if {@code key} is {@code null}
     */
    ILogger with(String key, Object value);

    /**
     * Creates a logger with additional contextual fields.
     *
     * <p>The returned logger should retain the existing logger context and
     * include the supplied fields in subsequent log entries.</p>
     *
     * @param context contextual fields
     * @return a logger containing the additional context
     * @throws NullPointerException if {@code context} is {@code null}
     */
    ILogger with(Map<String, ?> context);

    /**
     * Changes the class or source prefix associated with this logger.
     *
     * @param prefix source prefix
     * @throws NullPointerException if {@code prefix} is {@code null}
     */
    void setClassPrefix(String prefix);

    /**
     * Sets the output path used by the logger implementation.
     *
     * <p>The interpretation of the path is implementation-specific. A
     * {@code null} path may be used to indicate that no file output path
     * is configured.</p>
     *
     * @param path output path, or {@code null} to clear the configured path
     */
    void setPath(Path path);

    /**
     * Determines whether log entries at the specified level are currently
     * enabled.
     *
     * <p>This method can be used to avoid expensive message construction or
     * argument evaluation when a particular log level is disabled.</p>
     *
     * @param level level to check
     * @return {@code true} if entries at the specified level are enabled,
     *         otherwise {@code false}
     * @throws NullPointerException if {@code level} is {@code null}
     */
    boolean isEnabled(LogLevel level);
}