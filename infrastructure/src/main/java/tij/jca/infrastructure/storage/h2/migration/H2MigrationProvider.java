package tij.jca.infrastructure.storage.h2.migration;

import tij.jca.core.storage.exceptions.MigrationException;
import tij.jca.core.storage.migration.IMigration;
import tij.jca.core.storage.migration.IMigrationProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    private static final Pattern BLOCK_COMMENT_PATTERN = Pattern.compile(
            "/\\*.*?\\*/", Pattern.DOTALL
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
        return load(readIndex());
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

    private List<String> readIndex() {
        try (InputStream input = classLoader.getResourceAsStream(indexResource)) {
            if (input == null) {
                throw new MigrationException(
                        "Migration index resource not found: " + indexResource
                );
            }

            List<String> resources = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    resources.add(line);
                }
            }

            if (resources.isEmpty()) {
                throw new MigrationException(
                        "Migration index resource is empty: " + indexResource
                );
            }

            return List.copyOf(resources);
        } catch (IOException e) {
            throw new MigrationException(
                    "Failed to read migration index resource: " + indexResource,
                    e
            );
        }
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

            String rawSql = new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8
            );

            String sql = stripComments(rawSql);

            if (sql.isBlank()) {
                throw new MigrationException(
                        "Migration resource contains no executable SQL: " + resource
                );
            }

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

    private static String stripComments(String sql) {
        String withoutBlockComments = BLOCK_COMMENT_PATTERN.matcher(sql).replaceAll("");

        StringBuilder result = new StringBuilder();

        for (String line : withoutBlockComments.split("\n", -1)) {
            int commentIndex = line.indexOf("--");
            String cleaned = commentIndex >= 0 ? line.substring(0, commentIndex) : line;

            if (!cleaned.isBlank()) {
                result.append(cleaned).append('\n');
            }
        }

        return result.toString();
    }
}
