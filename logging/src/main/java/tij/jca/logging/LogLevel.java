package tij.jca.logging;

/**
 * Severity levels supported by the logging API.
 *
 * <p>Each level has associated numeric severity. Higher values represent
 * increasingly severe events. The severity values are used to determine
 * whether a log entry meets the configured minimum logging level.</p>
 *
 * <p>The levels, ordered from least to most severe, are:
 * {@link #TRACE}, {@link #DEBUG}, {@link #INFO}, {@link #WARN},
 * {@link #ERROR}, and {@link #FATAL}.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public enum LogLevel {

    /**
     * Detailed diagnostic information, typically useful only when
     * troubleshooting or developing the application.
     */
    TRACE(0),

    /**
     * Diagnostic information useful for developers when investigating
     * application behaviour.
     */
    DEBUG(10),

    /**
     * General information about normal application operation.
     */
    INFO(20),

    /**
     * An abnormal or potentially problematic condition that does not
     * necessarily prevent the application from continuing.
     */
    WARN(30),

    /**
     * An error that indicates a failure while performing an operation.
     */
    ERROR(40),

    /**
     * A critical error indicating that the application or a major component
     * may no longer be able to operate correctly.
     */
    FATAL(50);

    private final int severity;

    /**
     * Creates a log level with the specified severity.
     *
     * @param severity numeric severity used for level comparison
     */
    LogLevel(int severity) {
        this.severity = severity;
    }

    /**
     * Determines whether this level is at least as severe as the supplied
     * level.
     *
     * <p>For example, {@code ERROR.isAtLeast(WARN)} returns {@code true},
     * while {@code DEBUG.isAtLeast(INFO)} returns {@code false}.</p>
     *
     * @param other level to compare against
     * @return {@code true} if this level is at least as severe as
     *         {@code other}
     * @throws NullPointerException if {@code other} is {@code null}
     */
    public boolean isAtLeast(LogLevel other) {
        return severity >= other.severity;
    }

    /**
     * Returns the numeric severity associated with this log level.
     *
     * <p>Higher values indicate more severe log levels.</p>
     *
     * @return numeric severity
     */
    public int severity() {
        return severity;
    }
}