package tij.jca.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class ConsoleLogger implements ILogger {
    private final Consumer<String> sink;
    private final Map<String, Object> context;
    private String classPrefix = "";
    private Path path;

    public ConsoleLogger() {
        this(System.out::println);
    }

    public ConsoleLogger(Consumer<String> sink) {
        this(sink, Map.of());
    }

    private ConsoleLogger(Consumer<String> sink, Map<String, Object> context) {
        this.sink = Objects.requireNonNull(sink, "sink");
        this.context = Map.copyOf(context);
    }

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
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(message, "message");

        if (isEnabled(level)) {
            emit(level, null, message, args);
        }
    }

    @Override
    public void forceLog(String message, Object... args) {
        Objects.requireNonNull(message, "message");

        if (LoggerFactory.getConfig().enabled()) {
            emit(LogLevel.INFO, null, message, args);
        }
    }

    @Override
    public void log(LogLevel level, Throwable throwable, String message, Object... args) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(throwable, "throwable");
        Objects.requireNonNull(message, "message");

        if (isEnabled(level)) {
            emit(level, throwable, message, args);
        }
    }

    @Override
    public ILogger with(String key, Object value) {
        Objects.requireNonNull(key, "key");

        Map<String, Object> mergedContext = new LinkedHashMap<>(context);
        mergedContext.put(key, value);

        ConsoleLogger logger = new ConsoleLogger(sink, mergedContext);
        logger.classPrefix = classPrefix;
        logger.path = path;
        return logger;
    }

    @Override
    public ILogger with(Map<String, ?> additionalContext) {
        Objects.requireNonNull(additionalContext, "context");

        Map<String, Object> mergedContext = new LinkedHashMap<>(context);
        mergedContext.putAll(additionalContext);

        ConsoleLogger logger = new ConsoleLogger(sink, mergedContext);
        logger.classPrefix = classPrefix;
        logger.path = path;
        return logger;
    }

    @Override
    public void setClassPrefix(String prefix) {
        classPrefix = Objects.requireNonNull(prefix, "prefix");
    }

    @Override
    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public boolean isEnabled(LogLevel level) {
        Objects.requireNonNull(level, "level");

        ILogConfig config = LoggerFactory.getConfig();

        return config.enabled() && level.isAtLeast(config.minimumLogLevel());
    }

    private void emit(LogLevel level, Throwable throwable, String message, Object... args) {
        StringBuilder entry = new StringBuilder()
                .append('[').append(Instant.now()).append("] ")
                .append('[').append(level).append("] ");

        if (!classPrefix.isBlank()) {
            entry.append('[').append(classPrefix).append("] ");
        }

        entry.append(format(message, args));

        if (!context.isEmpty()) {
            entry.append(' ').append(context);
        }

        if (throwable != null) {
            entry.append(": ").append(throwable);

            if (LoggerFactory.getConfig().includeStackTraces()) {
                entry.append(System.lineSeparator()).append(stackTraceOf(throwable));
            }
        }

        sink.accept(entry.toString());
    }

    private static String format(String template, Object... args) {
        StringBuilder result = new StringBuilder();
        int argumentIndex = 0;
        int cursor = 0;

        while (argumentIndex < args.length) {
            int placeholder = template.indexOf("{}", cursor);

            if (placeholder < 0) {
                break;
            }

            result.append(template, cursor, placeholder)
                    .append(String.valueOf(args[argumentIndex++]));
            cursor = placeholder + 2;
        }

        result.append(template, cursor, template.length());

        while (argumentIndex < args.length) {
            result.append(' ')
                    .append(String.valueOf(args[argumentIndex++]));
        }

        return result.toString();
    }

    private static String stackTraceOf(Throwable throwable) {
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
