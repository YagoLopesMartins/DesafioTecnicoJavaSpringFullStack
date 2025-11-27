package com.desafiotecnico.matera.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record BalanceResponse (
        @JsonProperty("id") String accountId,
        String number,
        BigDecimal balance
) {}