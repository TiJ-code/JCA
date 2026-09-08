package tij.jca.core.entities;

import tij.jca.core.ids.MessageID;
import tij.jca.core.ids.UserID;
import tij.jca.core.utils.StringUtils;

import java.time.Instant;
import java.util.Objects;

public record Message(MessageID id, UserID authorId, Instant date, String text) {
    private static final int MAX_TEXT_LENGTH = 4192;

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
