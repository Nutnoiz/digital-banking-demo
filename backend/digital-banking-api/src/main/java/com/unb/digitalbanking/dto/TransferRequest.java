package com.unb.digitalbanking.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransferRequest(

        @NotNull(message = "Source account is required")
        Long sourceAccountId,

        @NotNull(message = "Destination account is required")
        Long destinationAccountId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        @DecimalMax(value = "9999999999999999.99", message = "Amount is too large")
        BigDecimal amount,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description
) {
}