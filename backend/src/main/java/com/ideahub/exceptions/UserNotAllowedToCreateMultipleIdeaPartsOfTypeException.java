package com.ideahub.exceptions;

public class UserNotAllowedToCreateMultipleIdeaPartsOfTypeException extends Exception {
    private static final long serialVersionUID = 8387610571107072792L;

    @Override
    public String getMessage() {
        return "User is attempting to create multiple idea parts of a type which only supports a single part.";
    }
}
