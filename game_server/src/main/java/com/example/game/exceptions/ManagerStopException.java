package com.example.game.exceptions;


public class ManagerStopException extends Exception {
    public ManagerStopException(String message) {
        super(message);
    }

    public ManagerStopException(String message, Throwable cause) {
        super(message, cause);
    }
}
