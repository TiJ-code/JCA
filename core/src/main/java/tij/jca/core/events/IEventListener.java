package tij.jca.core.events;

/**
 * The listener interface for receiving {@link  IEvent}
 * The class interested in a Type of {@link  IEvent} implements this interface and when
 * the Event occurs the {@link #eventPerformed(E)} is executed.
 *
 * @param <E> the type of {@link IEvent} this listener listens to
 *
 * @since 0.1.0
 * @author Jakob
 */
public interface IEventListener<E extends IEvent> {
    /**
     * Invoked when an Event occurs
     * @param event - the event to be processed
     */
    void eventPerformed(E event);
}
