package com.radwan.exception;

public class AccountCreationException extends Exception{
    private int errorCode;

    public AccountCreationException(int errorCode){
        super(AccountCreationErrorCodes.getFriendlyMessage(errorCode));
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
