package tij.jca.core.entities;

import tij.jca.core.ids.MessageID;
import tij.jca.core.ids.UserID;
import tij.jca.core.utils.StringUtils;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents an immutable message authored by a user.
 *
 * @param id the message identifier
 * @param authorId the author's user identifier
 * @param date the message creation time
 * @param text the message text
 * @since 0.1.0
 * @author TiJ
 */
public record Message(MessageID id, UserID authorId, Instant date, String text) {
    private static final int MAX_TEXT_LENGTH = 4192;

    /**
     * Creates a message after validating its required values and length.
     *
     * @throws NullPointerException if an identifier or date is null
     * @throws IllegalArgumentException if the text is blank or too long
     */
    public Message {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(authorId, "authorId");
        Objects.requireNonNull(date, "date");
        StringUtils.requireNonBlank(text, "text");

        if (text.length() > MAX_TEXT_LENGTH) {
            throw new IllegalArgumentException(
                    "Text must not be longer than " + MAX_TEXT_LENGTH + " characters."
            );
        }
    }
}
