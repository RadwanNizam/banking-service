package com.radwan.exception;

public class AccountTransferException extends Exception{

    private int errorCode;
    private String sourceAccountId;
    private String targetAccountId;

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
        this.sourceAccountId = fromAccountId;
        this.targetAccountId = toAccountId;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(String sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public String getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(String targetAccountId) {
        this.targetAccountId = targetAccountId;
    }
}
