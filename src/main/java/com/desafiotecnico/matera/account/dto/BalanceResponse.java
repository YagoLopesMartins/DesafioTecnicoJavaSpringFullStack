package com.desafiotecnico.matera.account.dto;

import java.math.BigDecimal;

public record BalanceResponse (
        String accountId,
        String number,
        BigDecimal balance
) {}