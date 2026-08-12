package tij.jca.core.ids;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UUIDGenTest {

    @Test
    void random() {
        String a = UUIDGen.random();
        String b = UUIDGen.random();
        Assertions.assertNotNull(a);
        Assertions.assertNotNull(b);
        Assertions.assertNotEquals(a,b);
    }
}