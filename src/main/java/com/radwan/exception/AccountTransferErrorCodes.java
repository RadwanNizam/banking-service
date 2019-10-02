package com.radwan.exception;

public final class AccountTransferErrorCodes {
    public static final int INVALID_SOURCE_ACCOUNT_ID = 0;
    public static final int SOURCE_ACCOUNT_DOES_NOT_EXIST = 1;
    public static final int INVALID_TARGET_ACCOUNT_ID = 2;
    public static final int TARGET_ACCOUNT_DOES_NOT_EXIST = 3;
    public static final int INVALID_TRANSFER_AMOUNT = 4;
    public static final int INSUFFICIENT_FUNDS = 5;
    public static final int UNABLE_TO_LOCK_CURRENT_ACCOUNT = 6;
    public static final int UNABLE_TO_LOCK_TARGET_ACCOUNT = 7;
    public static final int INTERNAL_ERROR = 8;

    private AccountTransferErrorCodes() {
    }

    public static String getFriendlyMessage(int code) {
        String message = null;

        switch (code) {
            case INVALID_SOURCE_ACCOUNT_ID:
                message = "Invalid 'from' account id"; break;
            case SOURCE_ACCOUNT_DOES_NOT_EXIST:
                message = "'from' account does not exist"; break;
            case INVALID_TARGET_ACCOUNT_ID:
                message = "Invalid 'to' account id"; break;
            case TARGET_ACCOUNT_DOES_NOT_EXIST:
                message = "'to' account does not exist"; break;
            case INVALID_TRANSFER_AMOUNT:
                message = "Invalid transfer amount"; break;
            case INSUFFICIENT_FUNDS:
                message = "insufficient funds"; break;
            case UNABLE_TO_LOCK_CURRENT_ACCOUNT:
                message = "unable to lock 'from' account"; break;
            case UNABLE_TO_LOCK_TARGET_ACCOUNT:
                message = "unable to lock 'to' account"; break;
            case INTERNAL_ERROR:
                message = "internal server error"; break;
            default:
                throw new IllegalArgumentException("Unknown code: " + code);
        }

        return message;
    }
}
