package com.radwan;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.radwan.exception.AccountAccessException;
import com.radwan.exception.AccountTransferErrorCodes;
import com.radwan.exception.AccountTransferException;
import com.radwan.model.Account;
import com.radwan.repository.AccountRepository;
import com.radwan.service.BankingService;
import com.radwan.service.BankingServiceImpl;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Random;

import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;

public class TestBankingServiceTransfer {

    private Injector injector;

    @Mock
    private AccountRepository accountRepository;

    private BankingService bankingService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private String sourceAccountId;
    private String targetAccountId;
    private Account sourceAccount;
    private Account targetAccount;
    private Random random = new Random();

    @BeforeClass
    public static void setupTest(){
    }

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(AccountRepository.class).toInstance(accountRepository);
            }
        });

        bankingService = injector.getInstance(BankingServiceImpl.class);
    }

    @Test
    public void testTransferMoney_source_account_id_is_null_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.INVALID_SOURCE_ACCOUNT_ID)));
        bankingService.transfer(null, "targetAccount", 100);
    }

    @Test
    public void testTransferMoney_source_account_id_is_empty_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.INVALID_SOURCE_ACCOUNT_ID)));
        bankingService.transfer("", "targetAccount", 100);
    }

    @Test
    public void testTransferMoney_source_account_does_not_exist_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.SOURCE_ACCOUNT_DOES_NOT_EXIST)));

        String targetAccountId = getRandomAccountId();
        Account targetAccount = Account.builder().id(targetAccountId).ownerFirstName("fname").ownerLastName("lname").
                balance(new BigDecimal(0)).build();
        Mockito.doReturn(targetAccount).when(accountRepository).find(targetAccountId);

        bankingService.transfer("sourceAccount", targetAccountId, 100);
    }

    @Test
    public void testTransferMoney_to_account_id_is_null_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.INVALID_TARGET_ACCOUNT_ID)));
        bankingService.transfer("sourceAccount", null, 100);
    }

    @Test
    public void testTransferMoney_to_account_id_is_empty_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.INVALID_TARGET_ACCOUNT_ID)));
        bankingService.transfer("sourceAccount", "", 100);
    }

    public void testTransferMoney_invalid_transfer_amount_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.INVALID_TRANSFER_AMOUNT)));
        bankingService.transfer("sourceAccount", "toAccount", -1);
    }

    @Test
    public void testTransferMoney_to_account_does_not_exist_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.TARGET_ACCOUNT_DOES_NOT_EXIST)));

        String sourceAccountId = getRandomAccountId();
        Account sourceAccount = Account.builder().id(sourceAccountId).ownerFirstName("fname").ownerLastName("lname").
                balance(new BigDecimal(0)).build();
        Mockito.doReturn(sourceAccount).when(accountRepository).find(sourceAccountId);
        bankingService.transfer(sourceAccountId, "toAccount", 100);
    }

    @Test
    public void testTransferMoney_insufficient_funds_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.INSUFFICIENT_FUNDS)));

        initAccounts(500d, 100d);
        BigDecimal transferAmount = new BigDecimal(600);
        bankingService.transfer(sourceAccountId, targetAccountId, transferAmount.doubleValue());
    }

    @Test
    public void testTransferMoney_success() throws AccountTransferException, AccountAccessException {
        initAccounts(500d, 100d);
        BigDecimal transferAmount = new BigDecimal(150);
        BigDecimal sourceAccountNewBalance = sourceAccount.getBalance().subtract(transferAmount);
        BigDecimal targetAccountNewBalance = targetAccount.getBalance().add(transferAmount);
        bankingService.transfer(sourceAccountId, targetAccountId, transferAmount.doubleValue());

        assertEquals(sourceAccountNewBalance, sourceAccount.getBalance());
        assertEquals(targetAccountNewBalance, targetAccount.getBalance());
    }

    private String getRandomAccountId(){
        return "account-" + random.nextLong();
    }

    private void initAccounts(double sourceAccountBalance, double targetAccountBalance){
        sourceAccountId = getRandomAccountId();
        targetAccountId = getRandomAccountId();
        sourceAccount = Account.builder().id(sourceAccountId).ownerFirstName("sourceFname").ownerLastName("sourceLname").
                balance(BigDecimal.valueOf(sourceAccountBalance)).build();
        targetAccount = Account.builder().id(targetAccountId).ownerFirstName("targetFname").ownerLastName("targetLname").
                balance(BigDecimal.valueOf(targetAccountBalance)).build();
        Mockito.doReturn(sourceAccount).when(accountRepository).find(sourceAccountId);
        Mockito.doReturn(targetAccount).when(accountRepository).find(targetAccountId);
    }
}