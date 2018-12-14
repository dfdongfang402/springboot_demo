package com.example.game.exceptions;


public class ManagerInitException extends Exception {
    public ManagerInitException(String message) {
        super(message);
    }

    public ManagerInitException(String message, Throwable cause) {
        super(message, cause);
    }
}
