package com.radwan.exception;

public final class AccountCreationErrorCodes {
    public static final int INVALID_FIRST_NAME = 0;
    public static final int INVALID_LAST_NAME = 1;


    private AccountCreationErrorCodes() {
    }

    public static String getFriendlyMessage(int code) {
        String message = null;

        switch (code) {
            case INVALID_FIRST_NAME :
                message = "Invalid 'first name'"; break;
            case INVALID_LAST_NAME :
                message = "Invalid 'last name'"; break;

            default:
                throw new IllegalArgumentException("Unknown code: " + code);
        }

        return message;
    }
}
