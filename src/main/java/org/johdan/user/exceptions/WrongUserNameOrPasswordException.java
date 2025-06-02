package org.johdan.user.exceptions;

public class WrongUserNameOrPasswordException extends RuntimeException {
    public WrongUserNameOrPasswordException(String message) {
        super(message);
    }
}
