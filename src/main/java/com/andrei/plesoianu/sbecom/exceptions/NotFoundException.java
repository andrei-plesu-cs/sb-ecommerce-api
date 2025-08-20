package com.andrei.plesoianu.sbecom.exceptions;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class NotFoundException extends RuntimeException {
    private final String resource;
    private final Long resourceId;

    public NotFoundException(@NonNull String resource, Long resourceId) {
        super(resourceId != null ?
                "%s resource with id %d not found".formatted(resource, resourceId) :
                "%s resource not found".formatted(resource));
        this.resource = resource;
        this.resourceId = resourceId;
    }

    public NotFoundException(@NonNull Class<?> resource, Long resourceId) {
        this(resource.getSimpleName(), resourceId);
    }
}
