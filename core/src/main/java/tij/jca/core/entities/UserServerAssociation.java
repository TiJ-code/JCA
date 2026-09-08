package tij.jca.core.entities;

import tij.jca.core.ids.ServerID;
import tij.jca.core.ids.UserID;
import tij.jca.core.utils.StringUtils;

import java.util.Objects;

/**
 * Associates a user with a server and an optional server-specific name.
 *
 * @param userId the user identifier
 * @param serverId the server identifier
 * @param serverDisplayName the optional server-specific display name
 * @since 0.1.0
 * @author TiJ
 */
public record UserServerAssociation(UserID userId, ServerID serverId, String serverDisplayName) {
    /**
     * Creates an association after validating its values.
     *
     * @throws NullPointerException if a required identifier is null
     * @throws IllegalArgumentException if the display name is blank
     */
    public UserServerAssociation {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(serverId, "serverId");

        if (serverDisplayName != null) {
            StringUtils.requireNonBlank(serverDisplayName, "serverDisplayName");
        }
    }
}
