package com.radwan;

import com.radwan.exception.AccountAccessException;
import com.radwan.exception.AccountTransferErrorCodes;
import com.radwan.exception.AccountTransferException;
import com.radwan.model.Account;
import com.radwan.repository.AccountRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;

public class TestAccount {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private AccountRepository accountRepository;
    private String sourceAccountId;
    private Account sourceAccount;
    private Account[] targetAccounts;
    private Random random = new Random();

    @Test
    public void testGetBalance_concurrent_access_success() throws InterruptedException{
        int targetAccountsCount = 20;
        ExecutorService ex = Executors.newFixedThreadPool(targetAccountsCount);
        CountDownLatch countDownLatch = new CountDownLatch(1);

        double accountBalance = 1000d;
        initSourceAccount(accountBalance);
        AtomicBoolean accountAccessExceptionThrown = new AtomicBoolean(Boolean.FALSE);

        for (int i = 0; i < targetAccountsCount; i++) {
            Runnable readBalanceTask = () ->
            {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    sourceAccount.getBalance();
                } catch (AccountAccessException e) {
                    accountAccessExceptionThrown.set(Boolean.TRUE);
                }
            };
            ex.submit(readBalanceTask);
        }
        // trigger the threads in executor service
        countDownLatch.countDown();
        ex.awaitTermination(10, TimeUnit.SECONDS);
        ex.shutdown();

        assertEquals(Boolean.FALSE, accountAccessExceptionThrown.get());
    }

    @Test
    /**
     * Multiple threads try to transfer money from 'sourceAccount'
     * The balance in 'sourceAccount' is enough to do only one transfer operation
     */
    public void testTransferMoney_sc1_success() throws AccountTransferException, InterruptedException, AccountAccessException {
        int targetAccountsCount = 20;
        ExecutorService ex = Executors.newFixedThreadPool(targetAccountsCount);
        CountDownLatch countDownLatch = new CountDownLatch(1);

        double sourceAccountBalance = 500d;
        double targetAccountBalance = 1000d;
        double transferAmount = 300d;
        double finalSourceAccountBalance = sourceAccountBalance - transferAmount;

        initAccounts(sourceAccountBalance, targetAccountBalance, targetAccountsCount);
        for (int i = 0; i < targetAccountsCount; i++) {
            Runnable transferMoneyTask = getTransferMoneyTask(countDownLatch,
                    sourceAccount, targetAccounts[i], transferAmount);
            ex.submit(transferMoneyTask);
        }

        // trigger the threads in executor service
        countDownLatch.countDown();
        ex.awaitTermination(10, TimeUnit.SECONDS);
        ex.shutdown();

        assertEquals(finalSourceAccountBalance, sourceAccount.getBalance().doubleValue(), 0);
    }

    @Test
    /**
     * Two threads will transfer money between two accounts only
     * thread 1 'sourceAccount' -> 'targetAccount'
     * thread 2 'targetAccount' -> 'sourceAccount'
     *
     * There should not be deadlock when thread1 is trying to lock 'targetAccount'
     * because that account was already locked by thread2
     */
    public void testTransferMoney_sc2_success() throws AccountTransferException, InterruptedException, AccountAccessException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch countDownLatch = new CountDownLatch(1);

        double sourceAccountBalance = 500d;
        double targetAccountBalance = 1000d;
        double transferAmount1 = 300d;
        double transferAmount2 = 100d;

        double finalSourceAccountBalance =
                sourceAccountBalance - transferAmount1 // sourceAccount -> targetAccount
                        + transferAmount2; // targetAccount -> sourceAccount

        initAccounts(sourceAccountBalance, targetAccountBalance, 1);
        Account targetAccount = targetAccounts[0];

        Runnable transferMoneyTask1 = getTransferMoneyTask(countDownLatch, sourceAccount, targetAccount, transferAmount1);
        executorService.submit(transferMoneyTask1);

        Runnable transferMoneyTask2 = getTransferMoneyTask(countDownLatch, targetAccount, sourceAccount, transferAmount2);
        executorService.submit(transferMoneyTask2);

        countDownLatch.countDown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
        executorService.shutdown();

        assertEquals(finalSourceAccountBalance, sourceAccount.getBalance().doubleValue(), 0);
    }

    /**
     * Initialize @sourceAccount and the Accounts objects in the array @targetAccountsCount
     */
    private void initAccounts(double sourceAccountBalance, double targetAccountBalance, int targetAccountsCount) {
        initSourceAccount(sourceAccountBalance);

        targetAccounts = new Account[targetAccountsCount];
        for (int i = 0; i < targetAccountsCount; i++) {
            String accountId = getRandomAccountId();
            targetAccounts[i] = Account.builder().id(accountId).ownerFirstName("targetFname" + i).ownerLastName("targetLname" + i).
                    balance(BigDecimal.valueOf(targetAccountBalance)).build();
        }
    }

    private void initSourceAccount(double sourceAccountBalance) {
        sourceAccountId = getRandomAccountId();
        sourceAccount = Account.builder().id(sourceAccountId).ownerFirstName("sourceFname").ownerLastName("sourceLname").
                balance(BigDecimal.valueOf(sourceAccountBalance)).build();
    }

    private Runnable getTransferMoneyTask(CountDownLatch countDownLatch, Account sourceAccount,
                                          Account targetAccount, double transferAmount) {
        return () -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                sourceAccount.transfer(targetAccount, transferAmount);
            } catch (AccountTransferException e) {
                if (e.getErrorCode() != AccountTransferErrorCodes.INSUFFICIENT_FUNDS) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private String getRandomAccountId() {
        return "account-" + random.nextLong();
    }
}