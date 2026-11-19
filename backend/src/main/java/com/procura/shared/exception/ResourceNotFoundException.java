package com.procura.shared.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Object identifier) {
        super("%s with identifier '%s' was not found".formatted(resourceName, identifier));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
