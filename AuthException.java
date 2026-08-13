package com.jagadeesh.jagadeeshcart.exception;

/** Thrown on login failure or duplicate registration. Maps to HTTP 401/409. */
public class AuthException extends Exception {

    public AuthException(String message) {
        super(message);
    }
}
