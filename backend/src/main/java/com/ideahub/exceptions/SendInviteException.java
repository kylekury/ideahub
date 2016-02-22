package com.ideahub.exceptions;

public class SendInviteException extends Exception {
    private static final long serialVersionUID = 8387610571103072792L;

    @Override
    public String getMessage() {
        return "Not able to send the invite";
    }
}
