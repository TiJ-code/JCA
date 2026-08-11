package tij.jca.core.storage.page;

/**
 * Represents the offset and limit used to request a page of results.
 *
 * <p>The offset specifies the number of elements to skip, while the limit
 * specifies the maximum number of elements to return.</p>
 *
 * @param offset the number of elements to skip before returning results
 * @param limit the maximum number of elements to include in the page
 *
 * @since 0.1.0
 * @author TiJ
 */
public record PageOffset(int offset, int limit) {}
