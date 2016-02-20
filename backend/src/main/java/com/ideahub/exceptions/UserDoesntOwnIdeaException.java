package com.ideahub.exceptions;

public class UserDoesntOwnIdeaException extends Exception {
    private static final long serialVersionUID = 8387610571107072792L;

    @Override
    public String getMessage() {
        return "User is attempting to load an idea they don't own.";
    }
}
