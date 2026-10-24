package  tij.jca.core.ids;
/**
 * Record for storing a User ID
 *
 * @param id The id of the User to be stored
 *
 * @since 0.1.0
 * @author Jakob
 */
public record UserID(String id) {
    /**
     * Creates a user identifier with a randomly generated version 4 UUID.
     */
    public UserID() {
        this(UUIDGen.random());
    }
}
