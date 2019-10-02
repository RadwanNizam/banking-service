package com.radwan;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.radwan.exception.AccountCreationException;
import com.radwan.model.Account;
import com.radwan.repository.AccountRepository;
import com.radwan.service.BankingService;
import com.radwan.service.BankingServiceImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.hamcrest.Matchers.containsString;

import java.math.BigDecimal;

public class TestBankingServiceCreateAccount {

    private Injector injector;

    @Mock
    private AccountRepository accountRepository;

    private BankingService bankingService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
    public void testCreateAccount_all_information_is_provided_success() throws AccountCreationException {
        String accountId = getRandomAccountId();
        Account account = Account.builder().id(accountId).ownerFirstName("fname").ownerLastName("lname").
               balance(new BigDecimal(0)).build();
        Mockito.doReturn(account).when(accountRepository).create(account);
        Account createdAccount = bankingService.create(account);

        assertNotNull(createdAccount.getId());
        assertEquals(createdAccount.getOwnerFirstName(), account.getOwnerFirstName());
        assertEquals(createdAccount.getOwnerLastName(), account.getOwnerLastName());
        assertEquals(createdAccount.getBalance(), account.getBalance());
    }

    @Test
    public void testCreateAccount_first_name_is_null_fail() throws AccountCreationException {
        thrown.expect(AccountCreationException.class);
        thrown.expectMessage(containsString("first name"));

        Account account = Account.builder().ownerFirstName(null).ownerLastName("lname").
                balance(new BigDecimal(0)).build();
        Account createdAccount = bankingService.create(account);
    }

    @Test
    public void testCreateAccount_empty_first_name_fail() throws AccountCreationException {
        thrown.expect(AccountCreationException.class);
        thrown.expectMessage(containsString("first name"));

        Account account = Account.builder().ownerFirstName("").ownerLastName("lname").
                balance(new BigDecimal(0)).build();
        Account createdAccount = bankingService.create(account);
    }

    @Test
    public void testCreateAccount_last_name_is_null_fail() throws AccountCreationException {
        thrown.expect(AccountCreationException.class);
        thrown.expectMessage(containsString("last name"));

        Account account = Account.builder().ownerFirstName("fname").ownerLastName(null).
                balance(new BigDecimal(0)).build();
        Account createdAccount = bankingService.create(account);
    }

    @Test
    public void testCreateAccount_empty_last_name_fail() throws AccountCreationException {
        thrown.expect(AccountCreationException.class);
        thrown.expectMessage(containsString("last name"));

        Account account = Account.builder().ownerFirstName("fname").ownerLastName("").
                balance(new BigDecimal(0)).build();
        Account createdAccount = bankingService.create(account);
    }

    private String getRandomAccountId(){
        return "account-" + System.currentTimeMillis();
    }
}