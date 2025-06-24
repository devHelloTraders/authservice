package com.traders.auth.exception;

public class ParentUserNotDefinedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ParentUserNotDefinedException() {
        super("Parent user not defined for new users!");
    }
}
