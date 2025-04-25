package com.example.bankingapp.service;

import com.example.bankingapp.dto.TransferDto;
import com.example.bankingapp.exception.BaseException;
import com.example.bankingapp.exception.TransferException;
import com.example.bankingapp.exception.UserNotFoundException;
import com.example.bankingapp.model.Account;
import com.example.bankingapp.model.User;
import com.example.bankingapp.repository.AccountRepository;
import com.example.bankingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transferMoney(Long fromUserId, TransferDto transferDto) {
        User toUser = userRepository.findByEmail(transferDto.getToUserEmail())
                .orElseThrow(() -> new UserNotFoundException(transferDto.getToUserEmail()));

        log.info("Initiate transfer from {} to {}, amount = {}", fromUserId, toUser.getId(), transferDto.getAmount());

        validateTransfer(fromUserId, toUser.getId(), transferDto.getAmount());

        try {
            Account from = accountRepository.findByUserIdWithLock(fromUserId)
                    .orElseThrow(() -> {
                        log.error("Sender's account not found: {}", fromUserId);
                        return new BaseException("Ваш аккаунт не найден", HttpStatus.NOT_FOUND);
                    });

            Account to = accountRepository.findByUserIdWithLock(toUser.getId())
                    .orElseThrow(() -> {
                        log.error("Recipient's account not found: {}", fromUserId);
                        return new BaseException("Аккаунт получателя не найден", HttpStatus.NOT_FOUND);
                    });

            from.setBalance(from.getBalance().subtract(transferDto.getAmount()));
            to.setBalance(to.getBalance().add(transferDto.getAmount()));

            accountRepository.save(from);
            accountRepository.save(to);

            log.info("Transfer completed from {} to {}, amount = {}", fromUserId, toUser.getId(), transferDto.getAmount());
        } catch (Exception ex) {
            log.error("Transfer failed between {} and {}: {}", fromUserId, toUser.getId(), ex.getMessage());
            throw new TransferException("Во время операции произошла неизвестная ошибка", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateTransfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        Account from = accountRepository.findByUserId(fromUserId)
                .orElseThrow(() -> {
                    log.error("Sender's account not found: {}", fromUserId);
                    return new BaseException("Ваш аккаунт не найден", HttpStatus.NOT_FOUND);
                });
        Account to = accountRepository.findByUserId(toUserId)
                .orElseThrow(() -> {
                    log.error("Recipient's account not found: {}", fromUserId);
                    return new BaseException("Аккаунт получателя не найден", HttpStatus.NOT_FOUND);
                });

        if (from.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient funds in account {}", fromUserId);
            throw new TransferException("Недостаточно средств", HttpStatus.PAYMENT_REQUIRED);
        }

        if (from.getId().equals(to.getId())) {
            log.warn("Attempt to transfer money with the same account: {}", fromUserId);
            throw new TransferException("Невозможно совершить перевод на один и тот же аккаунт", HttpStatus.CONFLICT);
        }
    }
}
