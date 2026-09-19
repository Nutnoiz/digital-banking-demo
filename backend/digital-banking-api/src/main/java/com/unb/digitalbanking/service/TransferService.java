package com.unb.digitalbanking.service;

import com.unb.digitalbanking.dto.TransferRequest;
import com.unb.digitalbanking.dto.TransferResponse;
import com.unb.digitalbanking.entity.Account;
import com.unb.digitalbanking.entity.Transaction;
import com.unb.digitalbanking.exception.AccountNotFoundException;
import com.unb.digitalbanking.exception.TransferException;
import com.unb.digitalbanking.repository.AccountRepository;
import com.unb.digitalbanking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransferResponse transfer(TransferRequest request) {

        validateRequest(request);

        /*
         * Lock accounts in deterministic ID order.
         *
         * This helps reduce deadlock risk when concurrent transfers
         * happen in opposite directions.
         */
        Long firstLockId = Math.min(
                request.sourceAccountId(),
                request.destinationAccountId()
        );

        Long secondLockId = Math.max(
                request.sourceAccountId(),
                request.destinationAccountId()
        );

        Account firstAccount = accountRepository.findByIdForUpdate(firstLockId)
                .orElseThrow(() ->
                        new AccountNotFoundException(firstLockId)
                );

        Account secondAccount = accountRepository.findByIdForUpdate(secondLockId)
                .orElseThrow(() ->
                        new AccountNotFoundException(secondLockId)
                );

        Account sourceAccount;
        Account destinationAccount;

        if (request.sourceAccountId().equals(firstAccount.getId())) {
            sourceAccount = firstAccount;
            destinationAccount = secondAccount;
        } else {
            sourceAccount = secondAccount;
            destinationAccount = firstAccount;
        }

        validateAccounts(sourceAccount, destinationAccount);

        BigDecimal amount = request.amount();

        BigDecimal sourceBalanceBefore = sourceAccount.getBalance();
        BigDecimal destinationBalanceBefore = destinationAccount.getBalance();

        if (sourceBalanceBefore.compareTo(amount) < 0) {
            throw new TransferException(
                    "Insufficient balance in source account"
            );
        }

        BigDecimal sourceBalanceAfter =
                sourceBalanceBefore.subtract(amount);

        BigDecimal destinationBalanceAfter =
                destinationBalanceBefore.add(amount);

        String referenceNo = generateReferenceNo();

        String description = request.description();

        sourceAccount.setBalance(sourceBalanceAfter);
        destinationAccount.setBalance(destinationBalanceAfter);

        Transaction debitTransaction = new Transaction();

        debitTransaction.setAccount(sourceAccount);
        debitTransaction.setTransactionType("TRANSFER_OUT");
        debitTransaction.setAmount(amount);
        debitTransaction.setBalanceBefore(sourceBalanceBefore);
        debitTransaction.setBalanceAfter(sourceBalanceAfter);
        debitTransaction.setReferenceNo(referenceNo);
        debitTransaction.setDescription(
                description != null && !description.isBlank()
                        ? description
                        : "Transfer to " + destinationAccount.getAccountNo()
        );

        Transaction creditTransaction = new Transaction();

        creditTransaction.setAccount(destinationAccount);
        creditTransaction.setTransactionType("TRANSFER_IN");
        creditTransaction.setAmount(amount);
        creditTransaction.setBalanceBefore(destinationBalanceBefore);
        creditTransaction.setBalanceAfter(destinationBalanceAfter);
        creditTransaction.setReferenceNo(referenceNo);
        creditTransaction.setDescription(
                description != null && !description.isBlank()
                        ? description
                        : "Transfer from " + sourceAccount.getAccountNo()
        );

        transactionRepository.saveAll(
                List.of(
                        debitTransaction,
                        creditTransaction
                )
        );

        return new TransferResponse(
                referenceNo,
                sourceAccount.getId(),
                sourceAccount.getAccountNo(),
                destinationAccount.getId(),
                destinationAccount.getAccountNo(),
                amount,
                sourceBalanceAfter,
                destinationBalanceAfter,
                "COMPLETED",
                LocalDateTime.now()
        );
    }

    private void validateRequest(TransferRequest request) {

        if (request.sourceAccountId()
                .equals(request.destinationAccountId())) {

            throw new TransferException(
                    "Source and destination accounts must be different"
            );
        }

        if (request.amount() == null
                || request.amount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new TransferException(
                    "Transfer amount must be greater than zero"
            );
        }

        if (request.amount().scale() > 2) {

            throw new TransferException(
                    "Transfer amount must have at most 2 decimal places"
            );
        }
    }

    private void validateAccounts(
            Account sourceAccount,
            Account destinationAccount
    ) {

        if (!"ACTIVE".equalsIgnoreCase(sourceAccount.getStatus())) {
            throw new TransferException(
                    "Source account is not active"
            );
        }

        if (!"ACTIVE".equalsIgnoreCase(destinationAccount.getStatus())) {
            throw new TransferException(
                    "Destination account is not active"
            );
        }

        if (!sourceAccount.getCurrency()
                .equalsIgnoreCase(destinationAccount.getCurrency())) {

            throw new TransferException(
                    "Source and destination currencies must match"
            );
        }
    }

    private String generateReferenceNo() {

        return "TRF-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();
    }
}