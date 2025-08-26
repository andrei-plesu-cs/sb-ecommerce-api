package com.andrei.plesoianu.sbecom.exceptions;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class NotFoundException extends RuntimeException {
    private final String resource;
    private final String resourceId;

    public NotFoundException(@NonNull String resource, Long resourceId) {
        super(resourceId != null ?
                "%s resource with id %d not found".formatted(resource, resourceId) :
                "%s resource not found".formatted(resource));
        this.resource = resource;
        this.resourceId = resourceId != null ? resourceId.toString() : "";
    }

    public NotFoundException(@NonNull Class<?> resource, Long resourceId) {
        this(resource.getSimpleName(), resourceId);
    }

    public NotFoundException(@NonNull String resource, String resourceName) {
        super(resourceName != null ?
                "%s resource with name %s not found".formatted(resource, resourceName) :
                "%s resource not found".formatted(resource));
        this.resource = resource;
        this.resourceId = resourceName;
    }

    public NotFoundException(@NonNull Class<?> resource, String resourceName) {
        this(resource.getSimpleName(), resourceName);
    }
}
