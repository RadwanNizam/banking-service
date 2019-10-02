package com.radwan.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.radwan.utils.ValidationUtil;
import com.radwan.exception.AccountCreationErrorCodes;
import com.radwan.exception.AccountCreationException;
import com.radwan.exception.AccountTransferErrorCodes;
import com.radwan.exception.AccountTransferException;
import com.radwan.model.Account;
import com.radwan.repository.AccountRepository;
import com.radwan.repository.AccountRepositoryImpl;

@Singleton
public class BankingServiceImpl implements BankingService{

    private AccountRepository accountRepository = new AccountRepositoryImpl();

    public BankingServiceImpl(){
    }

    @Inject
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountRepository getAccountRepository() {
        return accountRepository;
    }

    public Account create(Account account) throws AccountCreationException {
        if (!ValidationUtil.isValidName(account.getOwnerFirstName())){
            throw new AccountCreationException(AccountCreationErrorCodes.INVALID_FIRST_NAME);
        }

        if (!ValidationUtil.isValidName(account.getOwnerLastName())){
            throw new AccountCreationException(AccountCreationErrorCodes.INVALID_LAST_NAME);
        }

        return accountRepository.create(account);
    }

    public Account get(String accountId){
        return accountRepository.find(accountId);
    }

    @Override
    public void transfer(String fromAccountId, String toAccountId, double amount) throws AccountTransferException {
        if(!ValidationUtil.isValidAccountId(fromAccountId)){
            throw new AccountTransferException(AccountTransferErrorCodes.INVALID_FROM_ACCOUNT_ID);
        }

        if(!ValidationUtil.isValidAccountId(toAccountId)){
            throw new AccountTransferException(AccountTransferErrorCodes.INVALID_TO_ACCOUNT_ID);
        }

        Account fromAccount = accountRepository.find(fromAccountId);
        if(fromAccount == null){
            throw new AccountTransferException(AccountTransferErrorCodes.FROM_ACCOUNT_DOES_NOT_EXIST);
        }

        Account toAccount = accountRepository.find(toAccountId);
        if(toAccount == null){
            throw new AccountTransferException(AccountTransferErrorCodes.TO_ACCOUNT_DOES_NOT_EXIST);
        }

        fromAccount.transfer(toAccount, amount);

    }
}
