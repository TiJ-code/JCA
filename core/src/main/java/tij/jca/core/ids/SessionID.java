package tij.jca.core.ids;
/**
 * Record for storing a Session ID
 *
 * @param id The id of the Session to be stored
 *
 * @since 0.1.0
 * @author Jakob
 */
public record SessionID(String id) {
    /**
     * Creates a session identifier with a randomly generated version 4 UUID.
     */
    public SessionID() {
        this(UUIDGen.random());
    }
}
