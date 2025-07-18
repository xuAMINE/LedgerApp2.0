package com.example.bankapp.service;

import com.example.bankapp.model.Account;
import com.example.bankapp.model.Transaction;
import com.example.bankapp.model.User;
import com.example.bankapp.repository.AccountRepository;
import com.example.bankapp.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;


    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Account> getAccountsForUser(User user) {
        return accountRepository.findByUser(user);
    }

    public Account getUserAccountById(User user, Long accountId) {
        return accountRepository.findById(accountId)
                .filter(account -> account.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Account not found or doesn't belong to user"));
    }

    public void deposit(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    public void withdraw(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0 ) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public void transferAmount(Account fromAccount, Account toAccount, BigDecimal amount, String description) {
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        transactionRepository.save(Transaction.builder()
                .type("Transfer Out")
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .counterparty(toAccount.getUser().getUsername())
                .description(description)
                .account(fromAccount)
                .build());

        transactionRepository.save(Transaction.builder()
                .type("Transfer In")
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .counterparty(fromAccount.getUser().getUsername())
                .account(toAccount)
                .build());
    }



    public List<Transaction> getTransactionHistory(Account account) {
        return transactionRepository.findByAccountOrderByTimestamp(account);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }
}
