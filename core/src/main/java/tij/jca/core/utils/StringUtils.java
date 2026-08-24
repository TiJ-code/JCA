package tij.jca.core.utils;

/**
 * Utility methods for working with string values.
 *
 * <p>This class centralises common validation rules used across the project
 * so that callers can apply consistent argument checks without duplicating
 * boilerplate code.</p>
 *
 * @since 0.1.0
 * @author TiJ
 */
public final class StringUtils {
    /**
     * Prevents instantiation of this utility class.
     */
    private StringUtils() {
    }

    /**
     * Ensures that a string is not {@code null} and is not blank.
     *
     * <p>This is useful for validating required configuration values, names,
     * identifiers, and other text inputs that must not be empty.</p>
     *
     * @param value the value to validate
     * @param label the name of the value being validated, used in the exception
     *              message
     * @return the original string when it is valid
     * @throws IllegalArgumentException if the value is {@code null} or blank
     */
    public static String requireNonBlank(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return value;
    }
}
