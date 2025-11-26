package com.desafiotecnico.matera.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateAccountRequest (
    @NotBlank String number,
    @PositiveOrZero BigDecimal initialBalance
){}
