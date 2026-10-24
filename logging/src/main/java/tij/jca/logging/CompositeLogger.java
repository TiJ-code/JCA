package tij.jca.logging;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CompositeLogger implements ILogger {
    private final List<ILogger> delegates;

    public CompositeLogger(ILogger... delegates) {
        this(Arrays.asList(Objects.requireNonNull(delegates, "delegates")));
    }

    public CompositeLogger(List<ILogger> delegates) {
        Objects.requireNonNull(delegates, "delegates");

        if (delegates.isEmpty()) {
            throw new IllegalArgumentException("A composite logger requires at least one delegate");
        }

        List<ILogger> validatedDelegates = new ArrayList<>(delegates.size());

        for (ILogger delegate : delegates) {
            validatedDelegates.add(Objects.requireNonNull(delegate, "delegate"));
        }

        this.delegates = List.copyOf(validatedDelegates);
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

        for (ILogger delegate : delegates) {
            delegate.log(level, message, args);
        }
    }

    @Override
    public void forceLog(String message, Object... args) {
        Objects.requireNonNull(message, "message");

        for (ILogger delegate : delegates) {
            delegate.forceLog(message, args);
        }
    }

    @Override
    public void log(LogLevel level, Throwable throwable, String message, Object... args) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(throwable, "throwable");
        Objects.requireNonNull(message, "message");

        for (ILogger delegate : delegates) {
            delegate.log(level, throwable, message, args);
        }
    }

    @Override
    public ILogger with(String key, Object value) {
        Objects.requireNonNull(key, "key");

        return new CompositeLogger(
                delegates.stream()
                        .map(delegate -> delegate.with(key, value))
                        .toList()
        );
    }

    @Override
    public ILogger with(Map<String, ?> context) {
        Objects.requireNonNull(context, "context");

        return new CompositeLogger(
                delegates.stream()
                        .map(delegate -> delegate.with(context))
                        .toList()
        );
    }

    @Override
    public void setClassPrefix(String prefix) {
        Objects.requireNonNull(prefix, "prefix");

        for (ILogger delegate : delegates) {
            delegate.setClassPrefix(prefix);
        }
    }

    @Override
    public void setPath(Path path) {
        for (ILogger delegate : delegates) {
            delegate.setPath(path);
        }
    }

    @Override
    public boolean isEnabled(LogLevel level) {
        Objects.requireNonNull(level, "level");

        return delegates.stream()
                .anyMatch(delegate -> delegate.isEnabled(level));
    }
}
