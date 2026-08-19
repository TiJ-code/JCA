package tij.jca.logging;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Factory and global configuration entry point for application loggers.
 *
 * <p>{@code LoggerFactory} is responsible for creating logger instances and
 * maintaining the logging implementation and global logging configuration
 * used by the application.</p>
 *
 * <p>The configured logger factory and logging configuration are stored in
 * {@link AtomicReference atomic references}, allowing them to be safely
 * replaced while the application is running.</p>
 *
 * <p>By default, the factory uses {@link NoLogger}, meaning logging is
 * effectively disabled until an actual logger implementation is configured.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class LoggerFactory {

    private static final AtomicReference<Supplier<ILogger>> LOGGER_FACTORY =
            new AtomicReference<>(NoLogger::new);

    private static final AtomicReference<ILogConfig> CONFIG =
            new AtomicReference<>(
                    DefaultLogConfig.builder().build()
            );

    /**
     * Utility class; instantiation is not permitted.
     */
    private LoggerFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Creates a logger associated with the supplied class.
     *
     * <p>The fully qualified name of the class is used as the logger's
     * source prefix.</p>
     *
     * @param type class to associate with the logger
     * @return a logger associated with the supplied class
     * @throws NullPointerException if {@code type} is {@code null}
     */
    public static ILogger getLogger(Class<?> type) {
        Objects.requireNonNull(type, "type");

        return getLogger(type.getName());
    }

    /**
     * Creates a logger associated with the supplied name.
     *
     * <p>The configured logger factory is used to create the logger. The
     * logger is then initialized with the supplied source name and the
     * currently configured output path.</p>
     *
     * @param name name or source identifier associated with the logger
     * @return a newly created logger
     * @throws NullPointerException if {@code name} is {@code null}
     */
    public static ILogger getLogger(String name) {
        Objects.requireNonNull(name, "name");

        ILogger logger = LOGGER_FACTORY.get().get();

        logger.setClassPrefix(name);
        logger.setPath(CONFIG.get().outputPath());

        return logger;
    }

    /**
     * Replaces the factory used to create logger instances.
     *
     * <p>The supplied {@link Supplier} is invoked whenever a new logger is
     * requested through {@link #getLogger(Class)} or
     * {@link #getLogger(String)}.</p>
     *
     * @param factory factory responsible for creating logger instances
     * @throws NullPointerException if {@code factory} is {@code null}
     */
    public static void setLoggerFactory(Supplier<ILogger> factory) {
        LOGGER_FACTORY.set(
                Objects.requireNonNull(factory, "factory")
        );
    }

    /**
     * Replaces the global logging configuration.
     *
     * <p>The supplied configuration affects subsequently created loggers.
     * Existing logger instances may retain the configuration state depending on
     * their implementation.</p>
     *
     * @param config global logging configuration
     * @throws NullPointerException if {@code config} is {@code null}
     */
    public static void setConfig(ILogConfig config) {
        CONFIG.set(
                Objects.requireNonNull(config, "config")
        );
    }

    /**
     * Returns the currently configured global logging configuration.
     *
     * @return the current logging configuration
     */
    public static ILogConfig getConfig() {
        return CONFIG.get();
    }
}