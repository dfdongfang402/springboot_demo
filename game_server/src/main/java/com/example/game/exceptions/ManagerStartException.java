package com.example.game.exceptions;


public class ManagerStartException extends Exception {
    public ManagerStartException(String message) {
        super(message);
    }

    public ManagerStartException(String message, Throwable cause) {
        super(message, cause);
    }
}
