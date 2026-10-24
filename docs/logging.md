# Logging

JCA exposes logging through the `tij.jca.logging.ILogger` interface. Production
code should depend on this interface rather than on a particular logging
backend. This keeps hosts free to choose their output destinations and to add
adapters for their own logging infrastructure.

## Creating loggers

Use `LoggerFactory` to create a logger for a class or named component:

```java
private static final ILogger LOGGER =
        LoggerFactory.getLogger(UserService.class);
```

```java
ILogger migrationLogger = LoggerFactory.getLogger("database.migration");
```

The factory assigns the fully qualified class name or supplied name as the
logger's source prefix. By default, it creates `ConsoleLogger` instances.

Hosts can choose another implementation during application startup:

```java
LoggerFactory.setLoggerFactory(ConsoleLogger::new);
```

Configure the factory before loading classes that create static loggers. A
logger captures its source prefix when it is created, while the console logger
reads the active global log configuration for every emitted entry.

## Levels and filtering

The supported levels, from least to most severe, are:

1. `TRACE` - detailed diagnostic information.
2. `DEBUG` - development and investigation information.
3. `INFO` - normal application operation.
4. `WARN` - an abnormal condition from which work can continue.
5. `ERROR` - a failed operation.
6. `FATAL` - a critical failure that may prevent continued operation.

`DefaultLogConfig` emits `INFO` and more severe entries by default. Configure
the global threshold and enablement through `LoggerFactory`:

```java
LoggerFactory.setConfig(
        DefaultLogConfig.builder()
                .minimumLogLevel(LogLevel.DEBUG)
                .enabled(true)
                .includeStackTraces(true)
                .build()
);
```

Use `isEnabled` to avoid calculating expensive diagnostic values:

```java
if (LOGGER.isEnabled(LogLevel.DEBUG)) {
    LOGGER.debug("Loaded {} records.", repository.loadAll().size());
}
```

`forceLog` bypasses the minimum-level filter, but it still respects global
enablement. Use it only for entries that must remain visible at every enabled
level, such as a successful startup or a fatal shutdown notice.

## Message templates

Log messages use sequential `{}` placeholders:

```java
LOGGER.info("User {} joined server {}.", userId, serverId);
```

The console logger replaces each placeholder with the next argument. Extra
arguments are appended to the message separated by spaces. Placeholders with
no corresponding value remain unchanged. Prefer templates over string
concatenation so a logger can avoid unnecessary formatting when a level is
disabled.

## Contextual logging

Use `with` to derive a logger containing information that should appear in
every related entry:

```java
ILogger requestLogger = LOGGER
        .with("requestId", requestId)
        .with("userId", userId);

requestLogger.info("Request accepted.");
requestLogger.debug("Loading conversations.");
```

`with` returns a new logger. It does not mutate the original logger, so a
class-level logger remains safe to reuse for independent operations.

Multiple fields can be added at once:

```java
ILogger syncLogger = LOGGER.with(
        Map.of("serverId", serverId, "deviceId", deviceId)
);
```

## Exception logging

Pass the exception separately from the message:

```java
try {
    storageEngine.open();
} catch (StorageException exception) {
    LOGGER.error(exception, "Could not open storage.");
    throw exception;
}
```

This preserves both the operation context and the exception. The entry always
contains the exception summary. Configure
`DefaultLogConfig.Builder#includeStackTraces(boolean)` to control whether the
full stack trace is included.

Logging does not replace error handling. Log a failure where it adds useful
operational context, then propagate or translate the exception according to
the calling API's contract.

## Disabling logging

Use `NoLogger` when an embedding application must discard every entry:

```java
LoggerFactory.setLoggerFactory(NoLogger::new);
```

`NoLogger` performs no formatting or output, reports every level as disabled,
and returns itself from contextual logging methods.

## Console and file destinations

`ConsoleLogger` is the built-in output implementation. It writes formatted
entries to standard output:

```java
LoggerFactory.setLoggerFactory(ConsoleLogger::new);
```

The `ILogger#setPath(Path)` contract and `ILogConfig#outputPath()` reserve an
output path for implementations that write files. `FileLogger` is not yet
implemented, so setting `outputPath` does not currently enable file output.
When a file-backed logger is added, hosts will be able to configure its
destination through the existing configuration API:

```java
LoggerFactory.setConfig(
        DefaultLogConfig.builder()
                .outputPath(Path.of("logs", "jca.log"))
                .build()
);
```

## Multiple destinations and external adapters

`CompositeLogger` forwards each operation to its delegates in constructor
order. It can combine the console logger with a future file logger or an
adapter supplied by a host:

```java
LoggerFactory.setLoggerFactory(() ->
        new CompositeLogger(
                new ConsoleLogger(),
                new HostProvidedLogger()
        )
);
```

An SLF4J adapter should implement `ILogger` in the host or an optional
integration module. Do not add an SLF4J dependency to the core `logging`
module: JCA must remain usable without choosing a particular SLF4J version or
binding.
