package tij.jca.core.ids;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Wrapper class for generating pseudo random UUIDs
 *
 * @since 0.1.0
 * @author Jakob
 */
public final class UUIDGen {
    private static final SecureRandom UUID_V7_RANDOM = new SecureRandom();

    private static long lastUuidV7Timestamp = -1L;
    private static int lastUuidV7RandA;

    /**
     * Static factory for generating pseudo random UUID {@link String}
     * using the {@link UUID#randomUUID()} method.
     *
     * @return A randomly generated UUI as a {@link String}
     */
    public static String random() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a version 7 UUID string using the current Unix timestamp and
     * securely generated random bits.
     *
     * @return A generated version 7 UUID as a {@link String}
     */
    public static synchronized String randomV7() {
        long timestamp = System.currentTimeMillis();
        int randA;

        if (timestamp > lastUuidV7Timestamp) {
            randA = UUID_V7_RANDOM.nextInt(1 << 12);
        } else {
            timestamp = lastUuidV7Timestamp;
            randA = (lastUuidV7RandA + 1) & 0x0FFF;

            if (randA == 0) {
                timestamp++;
            }
        }

        lastUuidV7Timestamp = timestamp;
        lastUuidV7RandA = randA;

        long mostSignificantBits = (timestamp & 0xFFFF_FFFF_FFFFL) << 16;
        mostSignificantBits |= 0x7000L;

        long leastSignificantBits = UUID_V7_RANDOM.nextLong() & 0x3FFF_FFFF_FFFF_FFFFL;
        leastSignificantBits |= 0x8000_0000_0000_0000L;

        return new UUID(mostSignificantBits, leastSignificantBits).toString();
    }

    private UUIDGen() {}
}
