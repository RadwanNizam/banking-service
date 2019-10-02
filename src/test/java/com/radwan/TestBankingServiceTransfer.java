package com.radwan;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
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
import static org.junit.Assert.assertNotNull;

public class TestBankingServiceTransfer {

    private Injector injector;

    @Mock
    private AccountRepository accountRepository;

    private BankingService bankingService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private String fromAccountId;
    private String toAccountId;
    private Account from;
    private Account to;
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
    public void testTransferMoney_from_account_id_is_null_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.INVALID_FROM_ACCOUNT_ID)));
        bankingService.transfer(null, "toaccount", 100);
    }

    @Test
    public void testTransferMoney_from_account_id_is_empty_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.INVALID_FROM_ACCOUNT_ID)));
        bankingService.transfer("", "toaccount", 100);
    }

    @Test
    public void testTransferMoney_from_account_does_not_exist_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.FROM_ACCOUNT_DOES_NOT_EXIST)));

        String toAccountId = getRandomAccountId();
        Account toAccount = Account.builder().id(toAccountId).ownerFirstName("fname").ownerLastName("lname").
                balance(new BigDecimal(0)).build();
        Mockito.doReturn(toAccount).when(accountRepository).find(toAccountId);

        bankingService.transfer("fromAccount", toAccountId, 100);
    }

    @Test
    public void testTransferMoney_to_account_id_is_null_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.INVALID_TO_ACCOUNT_ID)));
        bankingService.transfer("fromAccount", null, 100);
    }

    @Test
    public void testTransferMoney_to_account_id_is_empty_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.INVALID_TO_ACCOUNT_ID)));
        bankingService.transfer("fromAccount", "", 100);
    }

    public void testTransferMoney_invalid_transfer_amount_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.INVALID_TRANSFER_AMOUNT)));
        bankingService.transfer("fromAccount", "toAccount", -1);
    }

    @Test
    public void testTransferMoney_to_account_does_not_exist_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.TO_ACCOUNT_DOES_NOT_EXIST)));

        String fromAccountId = getRandomAccountId();
        Account fromAccount = Account.builder().id(fromAccountId).ownerFirstName("fname").ownerLastName("lname").
                balance(new BigDecimal(0)).build();
        Mockito.doReturn(fromAccount).when(accountRepository).find(fromAccountId);
        bankingService.transfer(fromAccountId, "toAccount", 100);
    }

    @Test
    public void testTransferMoney_insufficient_funds_fail() throws AccountTransferException {
        thrown.expect(AccountTransferException.class);
        thrown.expect(hasProperty("errorCode", CoreMatchers.is(AccountTransferErrorCodes.INSUFFICIENT_FUNDS)));

        initAccounts(500d, 100d);
        BigDecimal transferAmount = new BigDecimal(600);
        bankingService.transfer(fromAccountId, toAccountId, transferAmount.doubleValue());
    }

    @Test
    public void testTransferMoney_success() throws AccountTransferException {
        initAccounts(500d, 100d);
        BigDecimal transferAmount = new BigDecimal(150);
        BigDecimal fromNewBalance = from.getBalance().subtract(transferAmount);
        BigDecimal toNewBalance = to.getBalance().add(transferAmount);
        bankingService.transfer(fromAccountId, toAccountId, transferAmount.doubleValue());

        assertEquals(fromNewBalance, from.getBalance());
        assertEquals(toNewBalance, to.getBalance());
    }

    private String getRandomAccountId(){
        return "account-" + random.nextLong();
    }

    private void initAccounts(double fromAccountBalance, double toAccountBalance){
        fromAccountId = getRandomAccountId();
        toAccountId = getRandomAccountId();
        from = Account.builder().id(fromAccountId).ownerFirstName("fromFname").ownerLastName("fromLname").
                balance(BigDecimal.valueOf(fromAccountBalance)).build();
        to = Account.builder().id(toAccountId).ownerFirstName("toFname").ownerLastName("toLname").
                balance(BigDecimal.valueOf(toAccountBalance)).build();
        Mockito.doReturn(from).when(accountRepository).find(fromAccountId);
        Mockito.doReturn(to).when(accountRepository).find(toAccountId);
    }
}