package com.unb.digitalbanking.dto;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNo,
        Long customerId,
        String customerName,
        String accountType,
        BigDecimal balance,
        String currency,
        String status
) {
}