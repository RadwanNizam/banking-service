package com.radwan;

import com.radwan.model.Account;
import com.radwan.repository.AccountRepository;
import com.radwan.repository.AccountRepositoryImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestAccountRepository {

    private AccountRepository accountRepository;

    @Before
    public void setup(){
        accountRepository = new AccountRepositoryImpl();
    }

    @Test
    public void testCreateAccount_success(){
        Account account = Account.builder().ownerFirstName("fname").ownerLastName("lname").build();
        account = accountRepository.create(account);
        assertNotNull(account.getId());
        assertEquals("fname", account.getOwnerFirstName());
        assertEquals("lname", account.getOwnerLastName());
    }

    @Test
    public void testCreateAccount_verify_unique_account_id_true(){
        Account account1 = Account.builder().ownerFirstName("fname1").ownerLastName("lname1").build();
        Account account2 = Account.builder().ownerFirstName("fname2").ownerLastName("lname2").build();
        account1 = accountRepository.create(account1);
        account2 = accountRepository.create(account2);
        assertNotEquals(account1.getId(), account2.getId());
    }

    @Test
    public void testFindAccount_success(){
        Account account = Account.builder().ownerFirstName("fname").ownerLastName("lname").build();
        account = accountRepository.create(account);
        Account searchedAccount = accountRepository.find(account.getId());
        assertEquals(searchedAccount, account);
    }

    @Test
    public void testFindAccount_account_does_not_exist_success(){
        Account searchedAccount = accountRepository.find("account-id");
        assertNull(searchedAccount);
    }
}
