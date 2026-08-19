package tij.jca.logging;

import java.nio.file.Path;

/**
 * Defines the global configuration used by the logging system.
 *
 * <p>The configuration controls whether logging is enabled, which log levels
 * are emitted, how exceptions are represented, and where log output may be
 * written.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public interface ILogConfig {

    /**
     * Returns the minimum log level that will be emitted.
     *
     * <p>Messages with severity below the returned level should be ignored
     * by logger implementations.</p>
     *
     * @return the minimum log level
     */
    LogLevel minimumLogLevel();

    /**
     * Returns whether logging is globally enabled.
     *
     * <p>When logging is disabled, logger implementations should avoid
     * performing unnecessary formatting or output operations.</p>
     *
     * @return {@code true} if logging is enabled, otherwise {@code false}
     */
    boolean enabled();

    /**
     * Returns whether exception stack traces should be included in log
     * output.
     *
     * @return {@code true} if stack traces should be included, otherwise
     * {@code false}
     */
    boolean includeStackTraces();

    /**
     * Returns the configured path for log output.
     *
     * <p>The interpretation of this path depends on the configured logging
     * implementation. A {@code null} value indicates that no output path
     * has been configured.</p>
     *
     * @return the configured output path, or {@code null} if none is set
     */
    Path outputPath();
}