package tij.jca.core.ids;

import java.util.UUID;

public class UUIDGen {
    public static String random() {
        return UUID.randomUUID().toString();
    }
}
