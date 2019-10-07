package com.radwan.repository;

import com.radwan.model.Account;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryAccountRepositoryImpl implements AccountRepository {
    private ConcurrentHashMap<String, Account> accountStore = new ConcurrentHashMap<>();
    private AtomicLong accountSequence = new AtomicLong(0L);

    @Override
    public Account create(Account account) {
        String accountId = String.valueOf(accountSequence.incrementAndGet());
        account.setId(accountId);
        account.setCreationDate(new Date());
        accountStore.put(account.getId(), account);
        return account;
    }

    @Override
    public Account find(String accountId) {
        return accountStore.get(accountId);
    }
}
