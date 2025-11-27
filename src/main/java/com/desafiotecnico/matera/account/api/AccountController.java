package com.desafiotecnico.matera.account.api;

import com.desafiotecnico.matera.account.domain.Account;
import com.desafiotecnico.matera.account.dto.BalanceResponse;
import com.desafiotecnico.matera.account.dto.CreateAccountRequest;
import com.desafiotecnico.matera.account.dto.TransactionBatchRequest;
import com.desafiotecnico.matera.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(
        name = "Accounts",
        description = "Operações de contas bancárias: criação, consulta de saldo e lançamentos de débito/crédito."
)
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @Operation(
            summary = "Criar conta bancária",
            description = "Cria uma nova conta bancária com número único e saldo inicial opcional."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Conta criada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BalanceResponse.class)
            )
    )

    @PostMapping
    public ResponseEntity<BalanceResponse> create(@Valid @RequestBody CreateAccountRequest request) {
        Account account = accountService.createAccount(request);

        BalanceResponse body = new BalanceResponse(
                account.getId(),
                account.getNumber(),
                account.getBalance()
        );

        URI location = URI.create("/api/accounts/" + account.getId());

        return ResponseEntity
                .created(location)
                .body(body);
    }


    @Operation(
            summary = "Aplicar lançamentos em lote",
            description = """
                    Aplica um ou mais lançamentos de débito/crédito em uma conta específica.
                    A operação é realizada de forma transacional e thread-safe, garantindo consistência de saldo em cenários concorrentes.
                    """
    )
    @ApiResponse(
            responseCode = "204",
            description = "Lançamentos aplicados com sucesso"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Conta não encontrada",
            content = @Content(schema = @Schema(implementation = com.desafiotecnico.matera.shared.error.ApiErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "422",
            description = "Saldo insuficiente",
            content = @Content(schema = @Schema(implementation = com.desafiotecnico.matera.shared.error.ApiErrorResponse.class))
    )
    @PostMapping("/{id}/transactions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void applyTransactions(
            @Parameter(description = "ID da conta (UUID)")
            @PathVariable String id,
            @Valid @RequestBody TransactionBatchRequest batch) {
        accountService.applyTransactionsWithRetry(id, batch);
    }

    @Operation(
            summary = "Obter saldo atual",
            description = "Retorna apenas o saldo atual da conta informada. Atalho para consultas rápidas."
    )
    @GetMapping("/{id}/balance")
    public BalanceResponse getBalance(
            @Parameter(description = "ID da conta (UUID)")
            @PathVariable String id)
    {
        return accountService.getBalance(id);
    }

    @Operation(
            summary = "Listar contas",
            description = "Retorna todas as contas cadastradas, incluindo as contas seed."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de contas retornada com sucesso"
    )
    @GetMapping
    public List<BalanceResponse> listAccounts() {
        return accountService.listAccounts();
    }
}
