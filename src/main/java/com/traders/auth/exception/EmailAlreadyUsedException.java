package com.traders.auth.exception;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class EmailAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyUsedException() {
        super("Email is already exist.");
    }
}
