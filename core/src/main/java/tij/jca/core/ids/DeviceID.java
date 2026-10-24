package tij.jca.core.ids;
/**
 * Record for storing a Device ID
 *
 * @param id The id of the Device to be stored
 *
 * @since 0.1.0
 * @author Jakob
 */
public record DeviceID(String id) {
    /**
     * Creates a device identifier with a randomly generated version 4 UUID.
     */
    public DeviceID() {
        this(UUIDGen.random());
    }
}
