package com.pungu.store.auth_service.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException {
    String resourceName;
    String fieldName;
    Object fieldValue;

    public ResourceNotFoundException(String fieldName, String resourceName, Object fieldValue) {
        super(String.format("%s is not found with %s: %s", fieldName, resourceName, fieldValue));
        this.fieldName = fieldName;
        this.resourceName = resourceName;
        this.fieldValue = fieldValue;
    }
}
