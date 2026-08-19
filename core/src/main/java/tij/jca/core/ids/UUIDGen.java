package tij.jca.core.ids;

import java.util.UUID;

/**
 * Wrapper class for generating pseudo random UUIDs
 *
 * @since 0.1.0
 * @author Jakob
 */
public class UUIDGen {
    /**
     * Static factory for generating pseudo random UUID {@link String}
     * using the {@link UUID#randomUUID()} method.
     *
     * @return A randomly generated UUI as a {@link String}
     */
    public static String random() {
        return UUID.randomUUID().toString();
    }
}
