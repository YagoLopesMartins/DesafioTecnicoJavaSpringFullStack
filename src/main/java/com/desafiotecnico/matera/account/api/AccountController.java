package com.desafiotecnico.matera.account.api;

import com.desafiotecnico.matera.account.domain.Account;
import com.desafiotecnico.matera.account.dto.BalanceResponse;
import com.desafiotecnico.matera.account.dto.CreateAccountRequest;
import com.desafiotecnico.matera.account.dto.TransactionBatchRequest;
import com.desafiotecnico.matera.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BalanceResponse create(@Valid @RequestBody CreateAccountRequest request) {
        Account account = accountService.createAccount(request);
        return new BalanceResponse(
                account.getId(),
                account.getNumber(),
                account.getBalance()
        );
    }

    @PostMapping("/{id}/transactions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void applyTransactions(@PathVariable String id, @Valid @RequestBody TransactionBatchRequest batch) {
        accountService.applyTransactions(id, batch);
    }

    @GetMapping("/{id}/balance")
    public BalanceResponse getBalance(@PathVariable String id) {
        return accountService.getBalance(id);
    }

    @GetMapping
    public List<BalanceResponse> listAccounts() {
        return accountService.listAccounts();
    }
}
