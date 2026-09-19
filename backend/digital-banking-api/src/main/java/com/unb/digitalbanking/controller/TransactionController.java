package com.unb.digitalbanking.controller;

import com.unb.digitalbanking.dto.TransactionResponse;
import com.unb.digitalbanking.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{accountId}/transactions")
    public List<TransactionResponse> getTransactions(
            @PathVariable Long accountId
    ) {
        return transactionService.getTransactionsByAccountId(accountId);
    }
}