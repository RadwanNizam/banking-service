package com.radwan.repository;

import com.radwan.model.Account;

public interface AccountRepository {
    Account create(Account account);
    Account find(String accountId);
}
