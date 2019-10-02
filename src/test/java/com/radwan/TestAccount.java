package com.radwan;

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

import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TestAccount {

    @Mock
    private AccountRepository accountRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private String fromAccountId;
    private Account fromAccount;
    private Account[] targetAccounts;
    private Random random = new Random();

    @Test
    /**
     * Multiple threads try to transfer money from 'fromAccount'
     * The balance in 'fromAccount' is enough to do only one transfer operation
     */
    public void testTransferMoney_sc1_success() throws AccountTransferException, InterruptedException {
        int targetAccountsCount = 20;
        ExecutorService ex = Executors.newFixedThreadPool(targetAccountsCount);
        CountDownLatch countDownLatch = new CountDownLatch(1);

        double fromAccountBalance = 500d;
        double toAccountBalance = 1000d;
        double transferAmount = 300d;
        double finalFromAccountBalance = fromAccountBalance - transferAmount;

        initAccounts(fromAccountBalance, toAccountBalance, targetAccountsCount);
        for(int i=0; i<targetAccountsCount; i++) {
            Runnable transferMoneyTask = getTransferMoneyTask(countDownLatch,
                    fromAccount, targetAccounts[i], transferAmount);
            ex.submit(transferMoneyTask);
        }

        // trigger the threads in executor service
        countDownLatch.countDown();
        ex.awaitTermination(10, TimeUnit.SECONDS);
        ex.shutdown();

        assertEquals(finalFromAccountBalance, fromAccount.getBalance().doubleValue(), 0);
    }

    @Test
    /**
     * Two threads are calling 'transform'.
     * thread 1 'fromAccount' -> 'targetAccount'
     * thread 2 'targetAccount' -> 'fromAccount'
     *
     * There should not be deadlock when thread1 is trying to lock 'targetAccount'
     * because that account was already locked by thread2
     */
    public void testTransferMoney_sc2_success() throws AccountTransferException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch countDownLatch = new CountDownLatch(1);

        double fromAccountBalance = 500d;
        double toAccountBalance = 1000d;
        double transferAmount1 = 300d;
        double transferAmount2 = 100d;

        double finalFromAccountBalance =
                fromAccountBalance - transferAmount1 // fromAccount -> toAccount
                + transferAmount2; // toAccount -> fromAccount

        initAccounts(fromAccountBalance, toAccountBalance, 1);
        Account targetAccount = targetAccounts[0];

        Runnable transferMoneyTask1 = getTransferMoneyTask(countDownLatch, fromAccount, targetAccount, transferAmount1);
        executorService.submit(transferMoneyTask1);

        Runnable transferMoneyTask2 = getTransferMoneyTask(countDownLatch, targetAccount, fromAccount, transferAmount2);
        executorService.submit(transferMoneyTask2);

        countDownLatch.countDown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
        executorService.shutdown();

        assertEquals(finalFromAccountBalance, fromAccount.getBalance().doubleValue(), 0);
    }

    /**
     * Initialize @fromAccount and the Accounts objects in the array @targetAccountsCount
     */
    private void initAccounts(double fromAccountBalance, double targetAccountBalance, int targetAccountsCount){
        fromAccountId = getRandomAccountId();
        fromAccount = Account.builder().id(fromAccountId).ownerFirstName("fromFname").ownerLastName("fromLname").
                balance(BigDecimal.valueOf(fromAccountBalance)).build();

        targetAccounts = new Account[targetAccountsCount];
        for(int i=0;i<targetAccountsCount;i++) {
            String accountId = getRandomAccountId();
            targetAccounts[i] = Account.builder().id(accountId).ownerFirstName("toFname" + i).ownerLastName("toLname" + i).
                    balance(BigDecimal.valueOf(targetAccountBalance)).build();
        }
    }

    private Runnable getTransferMoneyTask(CountDownLatch countDownLatch, Account fromAccount,
                                          Account targetAccount, double transferAmount) {
        return () -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {e.printStackTrace();}

            try {
                fromAccount.transfer(targetAccount, transferAmount);
            } catch(AccountTransferException e){
                if (e.getErrorCode() != AccountTransferErrorCodes.INSUFFICIENT_FUNDS){
                    e.printStackTrace();
                }
            }
            catch (Exception e){e.printStackTrace();}
        };
    }

    private String getRandomAccountId(){
        return "account-" + random.nextLong();
    }
}