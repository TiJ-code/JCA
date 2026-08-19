package tij.jca.logging;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Default immutable implementation of {@link ILogConfig}.
 *
 * <p>instances of this class are created through the {@link Builder},
 * which provides sensible defaults for all configuration options.</p>
 *
 * <p>The resulting configuration is immutable and can safely be shared
 * between logger instances.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class DefaultLogConfig implements ILogConfig {

    private final LogLevel minimumLogLevel;
    private final boolean enabled;
    private final boolean includeStackTraces;
    private final Path outputPath;

    /**
     * Creates a configuration from the supplied builder.
     *
     * @param builder builder containing the configuration values
     */
    private DefaultLogConfig(Builder builder) {
        this.minimumLogLevel = builder.minimumLogLevel;
        this.enabled = builder.enabled;
        this.includeStackTraces = builder.includeStackTraces;
        this.outputPath = builder.outputPath;
    }

    /**
     * Creates a new configuration builder.
     *
     * @return a new configuration builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the minimum log level that will be processed.
     *
     * <p>Messages below this level are ignored by logger implementations.</p>
     *
     * @return the minimum configured log level
     */
    @Override
    public LogLevel minimumLogLevel() {
        return minimumLogLevel;
    }

    /**
     * Returns whether logging is globally enabled.
     *
     * @return {@code true} if logging is enabled, otherwise {@code false}
     */
    @Override
    public boolean enabled() {
        return enabled;
    }

    /**
     * Returns whether stack traces should be included when logging
     * exceptions.
     *
     * @return {@code true} if stack traces should be included
     */
    @Override
    public boolean includeStackTraces() {
        return includeStackTraces;
    }

    /**
     * Returns the path used for log output.
     *
     * @return the configured output path, or {@code null} if no path was set
     */
    @Override
    public Path outputPath() {
        return outputPath;
    }

    /**
     * Builder for creating immutable {@link DefaultLogConfig} instances.
     *
     * <p>The builder uses the following defaults:</p>
     * <ul>
     *     <li>{@link LogLevel#INFO} as the minimum log level</li>
     *     <li>Logging enabled</li>
     *     <li>Stack traces enabled</li>
     *     <li>No output path</li>
     * </ul>
     */
    public static final class Builder {

        private LogLevel minimumLogLevel = LogLevel.INFO;
        private boolean enabled = true;
        private boolean includeStackTraces = true;
        private Path outputPath;

        /**
         * Sets the minimum log level.
         *
         * @param level minimum log level; must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code level} is {@code null}
         */
        public Builder minimumLogLevel(LogLevel level) {
            this.minimumLogLevel = Objects.requireNonNull(level);
            return this;
        }

        /**
         * Enables or disables logging.
         *
         * @param enabled {@code true} to enable logging
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * Configures whether exception stack traces should be included
         * in log output.
         *
         * @param includeStackTraces {@code true} to include stack traces
         * @return this builder
         */
        public Builder includeStackTraces(boolean includeStackTraces) {
            this.includeStackTraces = includeStackTraces;
            return this;
        }

        /**
         * Sets the path used for log output.
         *
         * @param path output path, or {@code null} to leave the path unset
         * @return this builder
         */
        public Builder outputPath(Path path) {
            this.outputPath = path;
            return this;
        }

        /**
         * Builds an immutable logging configuration.
         *
         * @return the resulting logging configuration
         */
        public ILogConfig build() {
            return new DefaultLogConfig(this);
        }
    }
}