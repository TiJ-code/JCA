package tij.jca.infrastructure.storage.h2.mapping;

import tij.jca.core.ids.UserID;

/**
 * Persistence representation of a row in the {@code users} table.
 *
 * @param id the stored user identifier
 * @param username the user's username
 * @param globalDisplayName the user's global display name
 *
 * @since 0.1.0
 * @author TiJ
 */
public record H2UserRow(
        UserID id,
        String username,
        String globalDisplayName
) {
}
