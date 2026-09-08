package tij.jca.core.entities;

import tij.jca.core.ids.ServerID;
import tij.jca.core.ids.UserID;
import tij.jca.core.utils.StringUtils;

import java.util.Objects;

public record UserServerAssociation(UserID userId, ServerID serverId, String serverDisplayName) {
    public UserServerAssociation {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(serverId, "serverId");

        if (serverDisplayName != null) {
            StringUtils.requireNonBlank(serverDisplayName, "serverDisplayName");
        }
    }
}
