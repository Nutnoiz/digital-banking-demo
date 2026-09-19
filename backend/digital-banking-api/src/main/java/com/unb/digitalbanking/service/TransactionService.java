package com.unb.digitalbanking.service;

import com.unb.digitalbanking.dto.TransactionResponse;
import com.unb.digitalbanking.entity.Transaction;
import com.unb.digitalbanking.repository.AccountRepository;
import com.unb.digitalbanking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {

        accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new RuntimeException("Account not found: " + accountId)
                );

        return transactionRepository
                .findByAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TransactionResponse toResponse(Transaction transaction) {

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccount().getId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getBalanceBefore(),
                transaction.getBalanceAfter(),
                transaction.getReferenceNo(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }
}