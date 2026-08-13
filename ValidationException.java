package com.jagadeesh.jagadeeshcart.exception;

/** Thrown by the service layer when input fails validation. Maps to HTTP 400. */
public class ValidationException extends Exception {

    private final String field;

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() { return field; }
}
