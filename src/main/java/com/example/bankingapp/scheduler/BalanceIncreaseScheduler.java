package com.example.bankingapp.scheduler;

import com.example.bankingapp.model.Account;
import com.example.bankingapp.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BalanceIncreaseScheduler {
    private final AccountRepository accountRepository;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void updateBalances() {
        log.info("Starting increase balances");
        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts) {
            BigDecimal maxAllowed = account.getInitialDeposit().multiply(new BigDecimal("2.07"));
            BigDecimal newBalance = account.getBalance().multiply(new BigDecimal("1.1"));

            if (newBalance.compareTo(maxAllowed) > 0) {
                newBalance = maxAllowed;
            }

            account.setBalance(newBalance);
            accountRepository.save(account);
        }

        log.info("Increasing accounts completed");
    }
}
