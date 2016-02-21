package com.ideahub.exceptions;

public class IdeaPartDoesntExistException extends Exception {
    private static final long serialVersionUID = 8387610571107072792L;

    @Override
    public String getMessage() {
        return "The requested idea part doesn't exist.";
    }
}
