package com.leeharkness.sma;

/**
 * Unauthenticated Exception
 */
public class SMAUnauthenticatedException extends Exception {
    public SMAUnauthenticatedException(String userName) {
        super("User [" + userName + "] is not authorized for action");
    }
}
