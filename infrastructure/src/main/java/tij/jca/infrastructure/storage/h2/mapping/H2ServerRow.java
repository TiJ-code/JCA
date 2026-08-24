package tij.jca.infrastructure.storage.h2.mapping;

import tij.jca.core.ids.ServerID;

/**
 * Persistence representation of a row in the {@code servers} table.
 *
 * @param id the stored server identifier
 * @param name the server name
 *
 * @since 0.1.0
 * @author TiJ
 */
public record H2ServerRow(
        ServerID id,
        String name
) {
}
