package com.desafiotecnico.matera.account.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record TransactionBatchRequest (
        @NotEmpty List<@Valid TransactionRequest> transactions
) {}