package com.radwan.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.radwan.exception.AccountAccessErrorCodes;
import com.radwan.exception.AccountAccessException;
import com.radwan.exception.AccountTransferErrorCodes;
import com.radwan.exception.AccountTransferException;
import com.radwan.lock.AccountLock;
import com.radwan.lock.JVMAccountLock;
import com.radwan.utils.ValidationUtil;
import lombok.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Consumes(MediaType.APPLICATION_JSON)
public class Account {

    private String id;
    private String ownerFirstName;
    private String ownerLastName;
    private BigDecimal balance;
    private Date creationDate;

    @JsonIgnore
    // Real instance of AccountLock can be either JVMAccountLock or ClusteredAccountLock
    private AccountLock accountLock = new JVMAccountLock();

    public void transfer(Account targetAccount, double amount) throws AccountTransferException {
        if(!ValidationUtil.isValidTransferAmoount(amount)){
            throw new AccountTransferException(AccountTransferErrorCodes.INVALID_TRANSFER_AMOUNT);
        }

        BigDecimal currentBalance = null;
        boolean sourceAccountLocked = false;
        boolean targetAccountLocked = false;

        try {
            sourceAccountLocked = accountLock.writeLock();

            if (!sourceAccountLocked) {
                throw new AccountTransferException(this.id, targetAccount.getId(),
                        AccountTransferErrorCodes.UNABLE_TO_LOCK_CURRENT_ACCOUNT);
            }

            targetAccountLocked = targetAccount.getAccountLock().writeLock();
            if (!targetAccountLocked) {
                throw new AccountTransferException(this.id, targetAccount.getId(),
                        AccountTransferErrorCodes.UNABLE_TO_LOCK_TARGET_ACCOUNT);
            }

            if (this.balance.doubleValue() < amount) {
                throw new AccountTransferException(this.id, targetAccount.getId(),
                        AccountTransferErrorCodes.INSUFFICIENT_FUNDS);
            }

            currentBalance = this.balance;
            this.balance = this.balance.subtract(BigDecimal.valueOf(amount));
            targetAccount.setBalance(targetAccount.getBalanceNoLock().add(BigDecimal.valueOf(amount)));
        } catch (Exception ex) {
            if (currentBalance != null) {
                this.balance = currentBalance;
            }

            if(ex instanceof AccountTransferException){
                throw ((AccountTransferException)ex);
            }else {
                throw new AccountTransferException(ex.getMessage(), ex, AccountTransferErrorCodes.INTERNAL_ERROR);
            }
        } finally {
            if (sourceAccountLocked) {
                this.getAccountLock().writeUnlock();
            }

            if (targetAccountLocked) {
                targetAccount.getAccountLock().writeUnlock();
            }
        }
    }

    public BigDecimal getBalance() throws AccountAccessException {
        try {
            accountLock.readLock();
            return balance;
        } catch (InterruptedException e) {
            throw new AccountAccessException(AccountAccessErrorCodes.READ_LOCK_TIME_OUT);
        } finally {
            accountLock.readUnlock();
        }
    }

    private BigDecimal getBalanceNoLock() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Required for the lombok library to initialize the locks automatically
     * when creating an Account
     */
    public static class AccountBuilder {
        private transient AccountLock accountLock = new JVMAccountLock();
    }
}
