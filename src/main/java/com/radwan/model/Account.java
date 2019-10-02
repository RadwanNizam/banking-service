package com.radwan.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.radwan.utils.ValidationUtil;
import com.radwan.exception.AccountTransferErrorCodes;
import com.radwan.exception.AccountTransferException;
import lombok.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;
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
    private static final long LOCK_TIME_OUT = 15;

    private String id;
    private String ownerFirstName;
    private String ownerLastName;
    private BigDecimal balance;
    private Date creationDate;

    @JsonIgnore
    private transient ReadWriteLock lock = new ReentrantReadWriteLock();
    @JsonIgnore
    private transient Lock writeLock = lock.writeLock();
    @JsonIgnore
    private transient Lock readLock = lock.readLock();

    public void transfer(Account to, double amount) throws AccountTransferException {
        if(!ValidationUtil.isValidTransferAmoount(amount)){
            throw new AccountTransferException(AccountTransferErrorCodes.INVALID_TRANSFER_AMOUNT);
        }

        BigDecimal currentBalance = null;
        boolean isLocked = false;
        boolean isToLocked = false;

        try {
            isLocked = this.writeLock.tryLock(LOCK_TIME_OUT, TimeUnit.SECONDS);

            if (!isLocked) {
                throw new AccountTransferException(this.id, to.getId(),
                        AccountTransferErrorCodes.UNABLE_TO_LOCK_CURRENT_ACCOUNT);
            }

            isToLocked = to.getWriteLock().tryLock(LOCK_TIME_OUT, TimeUnit.SECONDS);
            if (!isToLocked) {
                throw new AccountTransferException(this.id, to.getId(),
                        AccountTransferErrorCodes.UNABLE_TO_LOCK_TARGET_ACCOUNT);
            }

            if (this.balance.doubleValue() < amount) {
                throw new AccountTransferException(this.id, to.getId(),
                        AccountTransferErrorCodes.INSUFFICIENT_FUNDS);
            }

            currentBalance = this.balance;
            this.balance = this.balance.subtract(BigDecimal.valueOf(amount));
            to.setBalance(to.getBalanceNoLock().add(BigDecimal.valueOf(amount)));
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
            if (isLocked) {
                this.writeLock.unlock();
            }

            if (isToLocked) {
                to.getWriteLock().unlock();
            }
        }
    }

    public BigDecimal getBalance() {
        try {
            readLock.lock();
            return balance;
        } finally {
            readLock.unlock();
        }
    }

    private BigDecimal getBalanceNoLock() {
        return balance;
    }

    public static class AccountBuilder {
        private transient ReadWriteLock lock = new ReentrantReadWriteLock();
        private transient Lock writeLock = lock.writeLock();
        private transient Lock readLock = lock.readLock();
    }
}