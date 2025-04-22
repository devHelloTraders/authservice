package com.traders.auth.exception;

public class ContactNoAlreadyUsedException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public ContactNoAlreadyUsedException() {
        super("Contact Number is already in use.");
    }
}
