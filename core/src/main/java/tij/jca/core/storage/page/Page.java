package tij.jca.core.storage.page;

import java.util.List;

/**
 * Represents a single page of results returned from a paginated storage
 * operation.
 *
 * <p>A page contains the elements belonging to the requested page and
 * indicates whether additional elements are available after it.</p>
 *
 * @param <T> the type of elements contained in the page
 * @param content the elements contained in this page
 * @param hasNext {@code true} if another page of results is available;
 *                {@code false} otherwise
 *
 * @since 0.1.0
 * @author TiJ
 */
public record Page<T>(List<T> content, boolean hasNext) {}
