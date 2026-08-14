package tij.jca.core.events;

/**
 * Represents a registered subscription to an {@link IEvent}
 * <p>
 * A subscription can be canceled {@link #close()}. Once closed, the subscription will no longer receive {@code IEvent}
 * </p>
 *
 * @author Jakob
 * @since 0.1.0
 */
public interface EventSubscription extends AutoCloseable {

}
