package com.desafiotecnico.matera.account.repository;

import com.desafiotecnico.matera.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByNumber(String number);
}