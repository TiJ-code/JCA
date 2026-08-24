package tij.jca.infrastructure.storage.h2.mapping;

import tij.jca.core.ids.DeviceID;
import tij.jca.core.ids.UserID;

/**
 * Persistence representation of a row in the {@code device_user_mapping} table.
 *
 * @param userId the associated user identifier
 * @param deviceId the associated device identifier
 *
 * @since 0.1.0
 * @author TiJ
 */
public record H2Device2UserRow(
        UserID userId,
        DeviceID deviceId
) {
}
