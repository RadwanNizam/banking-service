package com.radwan.service;

import com.radwan.exception.AccountCreationException;
import com.radwan.exception.AccountTransferException;
import com.radwan.model.Account;

public interface BankingService {

    Account create(Account account) throws AccountCreationException;
    Account get(String accountId);
    void transfer(String fromAccountId, String toAccountId, double amount) throws AccountTransferException;
}
