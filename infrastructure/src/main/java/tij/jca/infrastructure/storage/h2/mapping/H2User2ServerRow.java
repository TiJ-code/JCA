package tij.jca.infrastructure.storage.h2.mapping;

import tij.jca.core.ids.ServerID;
import tij.jca.core.ids.UserID;

/**
 * Persistence representation of a row in the {@code user_server_mapping} table.
 *
 * @param serverId the associated server identifier
 * @param userId the associated user identifier
 * @param serverUsername the user's display name on that server
 *
 * @since 0.1.0
 * @author TiJ
 */
public record H2User2ServerRow(
        ServerID serverId,
        UserID userId,
        String serverUsername
) {
}
