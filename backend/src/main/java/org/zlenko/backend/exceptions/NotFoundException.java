package org.zlenko.backend.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AppException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public NotFoundException(String resource, Object id) {
        super(HttpStatus.NOT_FOUND, resource + " not found with id: " + id);
    }
}
