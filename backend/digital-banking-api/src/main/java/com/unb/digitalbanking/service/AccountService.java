package com.unb.digitalbanking.service;

import com.unb.digitalbanking.dto.AccountResponse;
import com.unb.digitalbanking.entity.Account;
import com.unb.digitalbanking.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {

        return accountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Account not found: " + id)
                );

        return toResponse(account);
    }

    private AccountResponse toResponse(Account account) {

        return new AccountResponse(
                account.getId(),
                account.getAccountNo(),
                account.getCustomer().getId(),
                account.getCustomer().getFirstName()
                        + " "
                        + account.getCustomer().getLastName(),
                account.getAccountType(),
                account.getBalance(),
                account.getCurrency(),
                account.getStatus()
        );
    }
}