package com.radwan.exception;

public class AccountTransferException extends Exception{

    private int errorCode;
    private String fromAccountId;
    private String toAccountId;

    public AccountTransferException(int errorCode){
        super(AccountTransferErrorCodes.getFriendlyMessage(errorCode));
        this.errorCode = errorCode;
    }

    public AccountTransferException(String message, Throwable cause, int errorCode){
        super(message, cause);
        this.errorCode = errorCode;
    }

    public AccountTransferException(String fromAccountId, String toAccountId, int errorCode){
        super(AccountTransferErrorCodes.getFriendlyMessage(errorCode));
        this.errorCode = errorCode;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(String toAccountId) {
        this.toAccountId = toAccountId;
    }
}
