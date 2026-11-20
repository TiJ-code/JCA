package tij.jca.core.ids;
/**
 * Record for storing a Message ID
 *
 * @param id The id of the Message to be stored
 *
 * @since 0.1.0
 * @author Jakob
 */
public record MessageID(String id) {
    /**
     * Creates a message identifier with a generated version 7 UUID.
     */
    public MessageID() {
        this(UUIDGen.randomV7());
    }
}
