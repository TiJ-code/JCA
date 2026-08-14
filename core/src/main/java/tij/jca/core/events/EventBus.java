package tij.jca.core.events;

import tij.jca.core.events.exceptions.EventException;

/**
 * Defines the minimal contract for an event bus in JCA
 * <p>
 * The Event bus manages subscription and dispatching for every type of {@link  IEvent}</p>
 *
 * @since 0.1.0
 * @author Jakob
 */
public interface EventBus {
    /**
     * Subscribes a {@link IEventListener} to a specific type of {@link IEvent}
     * and returns a new {@link EventSubscription}
     *
     * @param eventClass The concrete class of an {@link IEvent} implementation
     * @param eventListener The {@link IEventListener} listening for an {@link IEvent}
     * @return The new {@link EventSubscription}
     * @param <E> The type if {@link  IEvent}
     */
    <E extends IEvent> EventSubscription subscribe(Class<E> eventClass, IEventListener<E> eventListener);

    /**
     * Sends an {@link IEvent} to every subscribed endpoint and waits for an answer
     * @param event to be sent
     * @throws EventException thrown when dispatch fails
     */
    void dispatch(IEvent event);

    /**
     *Sends an {@link IEvent} to every subscribed endpoint without waiting for an answer
     * @param event - to be sent
     */
    void dispatchAsync(IEvent event);
}
