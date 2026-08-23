package tij.jca.infrastructure.storage.h2.migration;

import tij.jca.core.storage.exceptions.MigrationException;
import tij.jca.core.storage.migration.IMigration;
import tij.jca.core.storage.migration.IMigrationProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class H2MigrationProvider implements IMigrationProvider {
    private static final Pattern MIGRATION_PATTERN = Pattern.compile(
            "^p(\\d+)__([a-zA-Z0-9_-]+)\\.sql$"
    );

    private final String resourcePath;
    private final String indexResource;
    private final ClassLoader classLoader;

    public H2MigrationProvider(String resourcePath, String indexResource) {
        this(
                resourcePath,
                indexResource,
                Thread.currentThread().getContextClassLoader()
        );
    }

    public H2MigrationProvider(String resourcePath, String indexResource, ClassLoader classLoader) {
        this.resourcePath = normalizePath(
                Objects.requireNonNull(resourcePath, "resourcePath")
        );
        this.indexResource = normalizePath(
                Objects.requireNonNull(indexResource, "indexResource")
        );
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
    }

    @Override
    public List<IMigration> getMigrations() {
        throw new MigrationException("Classpath migration discovery requires a migration index."
            + " Please use the H2MigrationIndexProvider with an explicit resource list.");
    }

    public List<IMigration> load(List<String> resourceNames) {
        Objects.requireNonNull(resourceNames, "resourceNames");

        List<IMigration> migrations = new ArrayList<>();

        for (String resourceName : resourceNames) {
            migrations.add(loadMigration(resourceName));
        }

        migrations.sort(
                Comparator.comparingInt(IMigration::version)
        );

        return List.copyOf(migrations);
    }

    private IMigration loadMigration(String resourceName) {
        String fileName = resourceName.substring(
                resourceName.lastIndexOf('/') + 1
        );

        Matcher matcher = MIGRATION_PATTERN.matcher(fileName);

        if (!matcher.matches()) {
            throw new MigrationException(
                    "Invalid migration resource name: " + resourceName
            );
        }

        int version;

        try {
            version = Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException e) {
            throw new MigrationException(
                    "Invalid migration version: " + matcher.group(1),
                    e
            );
        }

        String description = matcher.group(2).replace('_', ' ');

        String resource = resourcePath + '/' + fileName;

        try (InputStream input = classLoader.getResourceAsStream(resource)) {
            if (input == null) {
                throw new MigrationException(
                        "Migration resource not found: " + resource
                );
            }

            String sql = new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8
            );

            return new H2SQLMigration(version, description, sql);
        } catch (IOException e) {
            throw new MigrationException(
                    "Failed to read migration resource: " + resource,
                    e
            );
        }
    }

    private static String normalizePath(String path) {
        String normalized = path.trim();

        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        if (normalized.isBlank()) {
            throw new IllegalArgumentException(
                    "resourcePath must not be blank."
            );
        }

        return normalized;
    }
}
