package tij.jca.infrastructure.storage.h2.mapping;

import tij.jca.core.ids.DeviceID;

/**
 * Persistence representation of a row in the {@code devices} table.
 *
 * @param id the stored device identifier
 * @param type the device type stored by H2
 *
 * @since 0.1.0
 * @author TiJ
 */
public record H2DeviceRow(
        DeviceID id,
        DeviceType type
) {
    /**
     * Device types supported by the H2 schema.
     */
    public enum DeviceType {
        DESKTOP,
        MOBILE
    }
}
