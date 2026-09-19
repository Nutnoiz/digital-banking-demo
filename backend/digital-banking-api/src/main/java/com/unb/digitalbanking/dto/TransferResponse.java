package com.unb.digitalbanking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse(
        String referenceNo,
        Long sourceAccountId,
        String sourceAccountNo,
        Long destinationAccountId,
        String destinationAccountNo,
        BigDecimal amount,
        BigDecimal sourceBalance,
        BigDecimal destinationBalance,
        String status,
        LocalDateTime createdAt
) {
}