package com.desafiotecnico.matera.account.repository;

import com.desafiotecnico.matera.account.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(String accountId);
}