package com.traders.auth.exception;

public class ContactNoAlreadyUsedException extends BadRequestAlertException{
    private static final long serialVersionUID = 1L;

    public ContactNoAlreadyUsedException() {
        super("Contact Number is in use!", "userManagement", "Contact Number exists!");
    }
}
