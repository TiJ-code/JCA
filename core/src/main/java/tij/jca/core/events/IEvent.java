package tij.jca.core.events;

import java.time.Instant;

/**
 * Defines the minimal contract for an Event in JCA.
 *
 * @since 0.1.0
 * @author Jakob
 */
public interface IEvent extends Comparable<IEvent>{
    /**
     *Returns the timestamp of the creation of this {@link IEvent}
     * @return the creation Timestamp
     */
    Instant getTimestamp();

    /**
     * Compares the Creation timestamp of this object to {@code other}
     *
     * @param other the object to be compared.
     * @return {@code -1} if this {@link IEvent} was created before {@code other},
     * {@code 0} if they were created at the same time and {@code 1} if it was created after
     */
    @Override
    int compareTo(IEvent other);
}
