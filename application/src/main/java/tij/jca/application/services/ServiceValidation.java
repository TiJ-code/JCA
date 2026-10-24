package tij.jca.application.services;

import tij.jca.application.exceptions.ResourceNotFoundException;
import tij.jca.application.exceptions.ValidationException;

/**
 * Provides shared input validation and missing-resource errors for application services.
 *
 * @since 0.1.0
 * @author TiJ
 */
final class ServiceValidation {
    private ServiceValidation() {}

    /**
     * Returns a value when it is not {@code null}.
     *
     * @param value the value to validate
     * @param parameterName the name used in the validation error
     * @param <T> the value type
     * @return the validated value
     * @throws ValidationException if the value is {@code null}
     */
    static <T> T requireNonNull(T value, String parameterName) {
        if (value == null) {
            throw new ValidationException(
                    parameterName + " must not be null."
            );
        }
        return value;
    }

    /**
     * Returns a string when it is neither {@code null} nor blank.
     *
     * @param value the value to validate
     * @param parameterName the name used in the validation error
     * @return the validated string
     * @throws ValidationException if the value is {@code null} or blank
     */
    static String requireNonBlank(String value, String parameterName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(
                    parameterName + " must not be blank"
            );
        }
        return value;
    }

    /**
     * Creates an exception describing a resource that could not be found.
     *
     * @param resourceName the name of the missing resource
     * @param identifier the identifier used to look up the resource
     * @return an exception describing the missing resource
     */
    static ResourceNotFoundException resourceNotFound(
            String resourceName,
            Object identifier
    ) {
        return new ResourceNotFoundException(
                resourceName + " with ID " + identifier + " was not found."
        );
    }
}
