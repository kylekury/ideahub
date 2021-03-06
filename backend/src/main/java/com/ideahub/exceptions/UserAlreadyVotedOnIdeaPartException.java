package com.ideahub.exceptions;

public class UserAlreadyVotedOnIdeaPartException extends Exception {
    private static final long serialVersionUID = -3412782950492405782L;

        @Override
    public String getMessage() {
        return "User is attempting to vote on an idea part they've already voted on.";
    }
}
