package tij.jca.core.utils;

public final class StringUtils {
    private StringUtils() {
    }

    public static String requireNonBlank(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return value;
    }
}
