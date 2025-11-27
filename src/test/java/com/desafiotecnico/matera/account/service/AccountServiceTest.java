package com.desafiotecnico.matera.account.service;

import com.desafiotecnico.matera.account.domain.Account;
import com.desafiotecnico.matera.account.domain.TransactionType;
import com.desafiotecnico.matera.account.dto.BalanceResponse;
import com.desafiotecnico.matera.account.dto.CreateAccountRequest;
import com.desafiotecnico.matera.account.dto.TransactionBatchRequest;
import com.desafiotecnico.matera.account.dto.TransactionRequest;
import com.desafiotecnico.matera.account.repository.AccountRepository;
import com.desafiotecnico.matera.account.repository.TransactionRepository;
import com.desafiotecnico.matera.shared.exception.InsufficientBalanceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @AfterEach
    void cleanup() {
        transactionRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
    }

    @Test
    void should_apply_credit_and_debit_in_batch() {
        Account account = accountService.createAccount(
                new CreateAccountRequest("9876-0", BigDecimal.valueOf(1000L))
        );

        TransactionBatchRequest batch = new TransactionBatchRequest(
                List.of(
                        new TransactionRequest(TransactionType.DEBIT, BigDecimal.valueOf(100L)),
                        new TransactionRequest(TransactionType.CREDIT, BigDecimal.valueOf(50L))
                )
        );

        accountService.applyTransactionsWithRetry(account.getId(), batch);

        BalanceResponse balance = accountService.getBalance(account.getId());
        assertThat(balance.balance()).isEqualByComparingTo("950.00");
    }

    @Test
    void should_throw_when_insufficient_balance() {
        Account account = accountService.createAccount(
                new CreateAccountRequest("9999-0", BigDecimal.valueOf(50L))
        );

        TransactionBatchRequest batch = new TransactionBatchRequest(
                List.of(
                        new TransactionRequest(TransactionType.DEBIT, BigDecimal.valueOf(100L))
                )
        );

        assertThrows(InsufficientBalanceException.class,
                () -> accountService.applyTransactionsWithRetry(account.getId(), batch));
    }

    @Test
    void should_handle_concurrent_transactions_on_same_account() throws Exception {
        Account account = accountService.createAccount(
                new CreateAccountRequest("ACC-CONCURRENT", BigDecimal.valueOf(1000L))
        );

        String accountId = account.getId();

        final int threads = 10;
        final int operationsPerThread = 20;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            tasks.add(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    TransactionBatchRequest batch = new TransactionBatchRequest(
                            List.of(
                                    new TransactionRequest(TransactionType.CREDIT, BigDecimal.TEN),
                                    new TransactionRequest(TransactionType.DEBIT, BigDecimal.valueOf(5L))
                            )
                    );
                    accountService.applyTransactionsWithRetry(accountId, batch);
                }
                return null;
            });
        }

        List<Future<Void>> futures = executor.invokeAll(tasks);
        executor.shutdown();
        boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);
        if (!finished) {
            executor.shutdownNow();
            fail("Executor did not finish within the timeout");
        }

        for (Future<Void> f : futures) {
            f.get(); // Propaga qualquer exceção das tasks
        }

        int totalBatches = threads * operationsPerThread;
        BigDecimal expectedDelta = BigDecimal.valueOf(totalBatches * 5L);
        BigDecimal expected = BigDecimal.valueOf(1000L).add(expectedDelta);

        BalanceResponse balance = accountService.getBalance(accountId);
        assertEquals(0, balance.balance().compareTo(expected));
    }

    @Test
    void concurrent_batches_keep_balance_consistent() throws Exception {
        Account acc = accountService.createAccount(
                new CreateAccountRequest("ACC-CONC-1", new BigDecimal("1000.00"))
        );

        TransactionBatchRequest batch = new TransactionBatchRequest(
                List.of(
                        new TransactionRequest(TransactionType.CREDIT, new BigDecimal("10.00")),
                        new TransactionRequest(TransactionType.DEBIT, new BigDecimal("5.00"))
                )
        );

        int threads = 10;
        int batchesPerThread = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads * batchesPerThread; i++) {
            executor.submit(() -> accountService.applyTransactionsWithRetry(acc.getId(), batch));
        }
        executor.shutdown();
        boolean finished = executor.awaitTermination(30, TimeUnit.SECONDS);
        if (!finished) {
            executor.shutdownNow();
            fail("Executor did not finish within the timeout");
        }

        Account updated = accountRepository.findById(acc.getId()).orElseThrow();

        BigDecimal expected = new BigDecimal("1000.00")
                .add(new BigDecimal("5.00").multiply(BigDecimal.valueOf(threads * batchesPerThread)));

        assertThat(updated.getBalance()).isEqualByComparingTo(expected);
    }
}
