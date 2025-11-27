package com.desafiotecnico.matera.config;

import com.desafiotecnico.matera.account.domain.Account;
import com.desafiotecnico.matera.account.domain.Transaction;
import com.desafiotecnico.matera.account.domain.TransactionType;
import com.desafiotecnico.matera.account.repository.AccountRepository;
import com.desafiotecnico.matera.account.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Bean
    @Transactional
    public org.springframework.boot.CommandLineRunner seedData() {
        return args -> {
            if (accountRepository.count() > 0) {
                return;
            }

            Account acc1 = Account.builder()
                    .number("ACC-1001")
                    .balance(BigDecimal.valueOf(1000.00))
                    .build();

            Account acc2 = Account.builder()
                    .number("ACC-2001")
                    .balance(BigDecimal.valueOf(500.00))
                    .build();

            Account acc3 = Account.builder()
                    .number("ACC-3001")
                    .balance(BigDecimal.ZERO)
                    .build();

            acc1 = accountRepository.save(acc1);
            acc2 = accountRepository.save(acc2);
            acc3 = accountRepository.save(acc3);

            Transaction t1 = Transaction.builder()
                    .account(acc1)
                    .type(TransactionType.CREDIT)
                    .amount(BigDecimal.valueOf(200.00))
                    .build();

            Transaction t2 = Transaction.builder()
                    .account(acc1)
                    .type(TransactionType.DEBIT)
                    .amount(BigDecimal.valueOf(50.00))
                    .build();

            acc1.credit(t1.getAmount());
            acc1.debit(t2.getAmount());

            transactionRepository.save(t1);
            transactionRepository.save(t2);
            accountRepository.save(acc1);
        };
    }
}
