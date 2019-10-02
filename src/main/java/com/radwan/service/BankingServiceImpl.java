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

    // The instance of AccountRepositoryImpl is created here
    // because of a conflict between 'guice' and 'dropwizard'
    // We will relay on Guice to initialize it in the next release
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
    public void transfer(String sourceAccountId, String targetAccountId, double amount) throws AccountTransferException {
        if(!ValidationUtil.isValidAccountId(sourceAccountId)){
            throw new AccountTransferException(AccountTransferErrorCodes.INVALID_SOURCE_ACCOUNT_ID);
        }

        if(!ValidationUtil.isValidAccountId(targetAccountId)){
            throw new AccountTransferException(AccountTransferErrorCodes.INVALID_TARGET_ACCOUNT_ID);
        }

        Account sourceAccount = accountRepository.find(sourceAccountId);
        if(sourceAccount == null){
            throw new AccountTransferException(AccountTransferErrorCodes.SOURCE_ACCOUNT_DOES_NOT_EXIST);
        }

        Account targetAccount = accountRepository.find(targetAccountId);
        if(targetAccount == null){
            throw new AccountTransferException(AccountTransferErrorCodes.TARGET_ACCOUNT_DOES_NOT_EXIST);
        }

        sourceAccount.transfer(targetAccount, amount);

    }
}
