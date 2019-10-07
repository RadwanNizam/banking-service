package com.radwan.exception;

public class AccountAccessException extends Exception{

    private int errorCode;

    public AccountAccessException(int errorCode){
        super(AccountAccessErrorCodes.getFriendlyMessage(errorCode));
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
