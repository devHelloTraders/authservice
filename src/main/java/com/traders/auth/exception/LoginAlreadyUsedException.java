package com.traders.auth.exception;

@SuppressWarnings("java:S110") // Inheritance tree of classes should not be too deep
public class LoginAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LoginAlreadyUsedException() {
        super("Login name is already in use.");
    }
}
