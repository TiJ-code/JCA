package tij.jca.core.events;

import java.time.Instant;

/**
 * Defines the base for every Event in JCA
 *
 * @author Jakob
 * @since 0.1.0
 */
public abstract class AbstractBaseEvent implements IEvent {
    protected final Instant timestamp;

    /**
     * Creates an {@link AbstractBaseEvent} and saves the creation timestamp
     */
    public AbstractBaseEvent() {
        timestamp = Instant.now();
    }

    /**
     * Returns the timestamp of creation for this {@link AbstractBaseEvent}
     *
     * @return the timestamp of creation for this {@link AbstractBaseEvent}
     */
    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Compares the Creation timestamp of this object to {@code other}
     *
     * @param other the object to be compared.
     * @return {@code -1} if this {@link AbstractBaseEvent} was created before {@code other},
     * {@code 0} if they were created at the same time and {@code 1} if it was created after
     */
    @Override
    public int compareTo(IEvent other) {
        return timestamp.compareTo(other.getTimestamp());
    }
}