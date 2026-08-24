package tij.jca.infrastructure.storage.h2.mapping;

import tij.jca.core.ids.MessageID;

import java.time.Instant;

/**
 * Persistence representation of a row in the {@code messages} table.
 *
 * @param id the stored message identifier
 * @param date the message creation timestamp
 * @param text the stored message text
 *
 * @since 0.1.0
 * @author TiJ
 */
public record H2MessageRow(
        MessageID id,
        Instant date,
        String text
) {
}
