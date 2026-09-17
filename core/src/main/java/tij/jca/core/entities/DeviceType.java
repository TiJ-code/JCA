package tij.jca.core.entities;

/**
 * Supported client device types.
 *
 * @since 0.1.0
 * @author TiJ
 */
public enum DeviceType {
    /**
     * A desktop client device.
     */
    DESKTOP,

    /**
     * A mobile client device.
     */
    MOBILE;

    /**
     * Returns the database representation of this device type.
     *
     * @return the lower-case database value
     */
    public String databaseValue() {
        return name().toLowerCase();
    }
}
