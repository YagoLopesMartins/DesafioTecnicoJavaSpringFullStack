package com.desafiotecnico.matera.account.dto;

import com.desafiotecnico.matera.account.domain.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequest (
        @NotNull TransactionType type,
        @NotNull @Positive BigDecimal amount
) {}
