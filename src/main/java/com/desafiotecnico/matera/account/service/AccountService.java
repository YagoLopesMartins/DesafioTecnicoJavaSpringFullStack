package com.desafiotecnico.matera.account.service;

import com.desafiotecnico.matera.account.domain.Account;
import com.desafiotecnico.matera.account.domain.Transaction;
import com.desafiotecnico.matera.account.domain.TransactionType;
import com.desafiotecnico.matera.account.dto.BalanceResponse;
import com.desafiotecnico.matera.account.dto.CreateAccountRequest;
import com.desafiotecnico.matera.account.dto.TransactionBatchRequest;
import com.desafiotecnico.matera.account.dto.TransactionRequest;
import com.desafiotecnico.matera.account.repository.AccountRepository;
import com.desafiotecnico.matera.account.repository.TransactionRepository;
import com.desafiotecnico.matera.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        Account account = Account.builder()
                .number(request.number())
                .balance(
                        request.initialBalance() != null ? request.initialBalance() : BigDecimal.ZERO
                )
                .build();
        return accountRepository.save(account);
    }

    @Transactional
    public void applyTransactions(String accountId, TransactionBatchRequest batch) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));

        for (TransactionRequest tr : batch.transactions()) {
            if (tr.type() == TransactionType.CREDIT) {
                account.credit(tr.amount());
            } else if (tr.type() == TransactionType.DEBIT) {
                account.debit(tr.amount());
            }

            Transaction tx = Transaction.builder()
                    .account(account)
                    .type(tr.type())
                    .amount(tr.amount())
                    .build();
            transactionRepository.save(tx);
        }

        accountRepository.save(account);
    }


    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Conta não encontrada"));

        return new BalanceResponse(account.getId(), account.getNumber(), account.getBalance());
    }

    @Transactional
    public void applyTransactionsWithRetry(String accountId, TransactionBatchRequest batch) {
        applyTransactions(accountId, batch);
    }

    @Transactional
    protected void doApplyTransactions(String accountId, TransactionBatchRequest batch) {
        applyTransactions(accountId, batch);
    }

    @Transactional(readOnly = true)
    public List<BalanceResponse> listAccounts() {
        return accountRepository.findAll().stream()
                .map(account -> new BalanceResponse(
                        account.getId(),
                        account.getNumber(),
                        account.getBalance()
                ))
                .collect(Collectors.toList());
    }
}
