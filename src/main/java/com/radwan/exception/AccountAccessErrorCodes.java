package com.radwan.exception;

public class AccountAccessErrorCodes {
    public static final int READ_LOCK_TIME_OUT = 0;


    private AccountAccessErrorCodes() {
    }

    public static String getFriendlyMessage(int code) {
        String message = null;

        switch (code) {
            case READ_LOCK_TIME_OUT :
                message = "Unable to lock the account for read, try again later"; break;
            default:
                throw new IllegalArgumentException("Unknown code: " + code);
        }

        return message;
    }
}
