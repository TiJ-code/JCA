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

/**
 * Loads H2 SQL migrations from classpath resources.
 *
 * <p>Migration resources use the {@code pVERSION__DESCRIPTION.sql} naming
 * convention and are listed in an index resource.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
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

    /**
     * Creates a provider using the current thread's context class loader.
     *
     * @param resourcePath directory containing migration resources
     * @param indexResource resource listing migration file names
     */
    public H2MigrationProvider(String resourcePath, String indexResource) {
        this(
                resourcePath,
                indexResource,
                Thread.currentThread().getContextClassLoader()
        );
    }

    /**
     * Creates a provider using the supplied class loader.
     *
     * @param resourcePath directory containing migration resources
     * @param indexResource resource listing migration file names
     * @param classLoader class loader used to read resources
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if either path is blank
     */
    public H2MigrationProvider(String resourcePath, String indexResource, ClassLoader classLoader) {
        this.resourcePath = normalizePath(
                Objects.requireNonNull(resourcePath, "resourcePath")
        );
        this.indexResource = normalizePath(
                Objects.requireNonNull(indexResource, "indexResource")
        );
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
    }

    /**
     * Loads all migrations listed by the configured index resource.
     *
     * @return migrations sorted by ascending version
     * @throws MigrationException if the index or a migration cannot be read
     */
    @Override
    public List<IMigration> getMigrations() {
        return load(readIndex());
    }

    /**
     * Loads and sorts migrations from the supplied resource names.
     *
     * @param resourceNames migration resource names
     * @return migrations sorted by ascending version
     * @throws NullPointerException if {@code resourceNames} is {@code null}
     * @throws MigrationException if a migration resource is invalid or cannot
     *                               be read
     */
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

    /**
     * Reads migration resource names from the configured index.
     *
     * @return non-empty migration resource names
     * @throws MigrationException if the index is missing, empty, or unreadable
     */
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

    /**
     * Loads and parses one migration resource.
     *
     * @param resourceName resource name from the migration index
     * @return the parsed migration
     * @throws MigrationException if the resource name or SQL is invalid, or
     *                               the resource cannot be read
     */
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

    /**
     * Normalises a classpath resource path by removing surrounding slashes.
     *
     * @param path resource path to normalise
     * @return normalised resource path
     * @throws IllegalArgumentException if the normalised path is blank
     */
    private static String normalizePath(String path) {
        String normalised = path.trim();

        while (normalised.startsWith("/")) {
            normalised = normalised.substring(1);
        }

        while (normalised.endsWith("/")) {
            normalised = normalised.substring(0, normalised.length() - 1);
        }

        if (normalised.isBlank()) {
            throw new IllegalArgumentException(
                    "resourcePath must not be blank."
            );
        }

        return normalised;
    }

    /**
     * Removes SQL block and line comments.
     *
     * @param sql SQL text to clean
     * @return SQL text without comments
     */
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
