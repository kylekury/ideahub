package com.ideahub.exceptions;

public class IdeaPartTypeNotFoundException extends Exception {
    private static final long serialVersionUID = 7807970534953553826L;

    @Override
    public String getMessage() {
        return "Attempted to look up an idea part type id that doesn't exist.";
    }
}
