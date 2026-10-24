package tij.jca.application.services;

import tij.jca.application.exceptions.ResourceNotFoundException;
import tij.jca.application.exceptions.ValidationException;

final class ServiceValidation {
    private ServiceValidation() {}

    static <T> T requireNonNull(T value, String parameterName) {
        if (value == null) {
            throw new ValidationException(
                    parameterName + " must not be null."
            );
        }
        return value;
    }

    static String requireNonBlank(String value, String parameterName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(
                    parameterName + " must not be blank"
            );
        }
        return value;
    }

    static ResourceNotFoundException resourceNotFound(
            String resourceName,
            Object identifier
    ) {
        return new ResourceNotFoundException(
                resourceName + " with ID " + identifier + " was not found."
        );
    }
}
