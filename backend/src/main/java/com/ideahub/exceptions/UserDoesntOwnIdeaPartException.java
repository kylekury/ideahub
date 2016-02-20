package com.ideahub.exceptions;

public class UserDoesntOwnIdeaPartException extends Exception {
    private static final long serialVersionUID = 8387610571107072792L;

    @Override
    public String getMessage() {
        return "User is attempting to update an idea part that they don't own.";
    }
}
