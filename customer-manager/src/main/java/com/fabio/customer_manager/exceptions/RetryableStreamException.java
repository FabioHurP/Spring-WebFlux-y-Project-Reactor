package com.fabio.customer_manager.exceptions;

public class RetryableStreamException extends RuntimeException {

    public RetryableStreamException(String message) {
        super(message);
    }
}
