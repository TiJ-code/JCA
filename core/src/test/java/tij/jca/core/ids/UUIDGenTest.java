package tij.jca.core.ids;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class UUIDGenTest {

    @Test
    void random() {
        String a = UUIDGen.random();
        String b = UUIDGen.random();
        Assertions.assertNotNull(a);
        Assertions.assertNotNull(b);
        Assertions.assertNotEquals(a,b);
    }

    @Test
    void randomV7() {
        UUID uuid = UUID.fromString(UUIDGen.randomV7());

        Assertions.assertEquals(7, uuid.version());
        Assertions.assertEquals(2, uuid.variant());

        long timestamp = uuid.getMostSignificantBits() >>> 16;
        Assertions.assertTrue(timestamp <= System.currentTimeMillis());
        Assertions.assertTrue(timestamp >= System.currentTimeMillis() - 1_000);
    }
}