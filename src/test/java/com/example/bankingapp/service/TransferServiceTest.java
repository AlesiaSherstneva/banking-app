package com.example.bankingapp.service;

import com.example.bankingapp.dto.TransferDto;
import com.example.bankingapp.exception.TransferException;
import com.example.bankingapp.model.Account;
import com.example.bankingapp.model.User;
import com.example.bankingapp.repository.AccountRepository;
import com.example.bankingapp.repository.UserRepository;
import com.example.bankingapp.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TransferServiceImpl.class)
@TestMethodOrder(MethodOrderer.Random.class)
class TransferServiceTest {
    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AccountRepository accountRepository;

    @Autowired
    private TransferService transferService;

    private User fromUser, toUser;
    private Account fromAccount, toAccount;

    @BeforeEach
    void setUp() {
        fromUser = User.builder().id(1L).build();
        fromAccount = Account.builder()
                .id(11L)
                .user(fromUser)
                .balance(new BigDecimal(10000))
                .build();

        toUser = User.builder().id(2L).build();
        toAccount = Account.builder()
                .id(22L)
                .user(toUser)
                .balance(new BigDecimal(1000))
                .build();
    }

    @Test
    void transferMoneySuccessTest() {
        TransferDto transferDto = TransferDto.builder()
                .toUserEmail("test@email.com")
                .amount(new BigDecimal(5000))
                .build();

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(toUser));
        when(accountRepository.findByUserId(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserId(2L)).thenReturn(Optional.of(toAccount));
        when(accountRepository.findByUserIdWithLock(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserIdWithLock(2L)).thenReturn(Optional.of(toAccount));

        transferService.transferMoney(1L, transferDto);

        assertThat(fromAccount.getBalance()).isEqualTo(new BigDecimal(5000));
        assertThat(toAccount.getBalance()).isEqualTo(new BigDecimal(6000));

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(accountRepository, times(2)).findByUserId(anyLong());
        verify(accountRepository, times(2)).findByUserIdWithLock(anyLong());
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void transferMoneyInsufficientFundsTest() {
        TransferDto transferDto = TransferDto.builder()
                .toUserEmail("test@email.com")
                .amount(new BigDecimal(15000))
                .build();

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(toUser));
        when(accountRepository.findByUserId(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByUserId(2L)).thenReturn(Optional.of(toAccount));

        TransferException thrown = assertThrows(TransferException.class,
                () -> transferService.transferMoney(1L, transferDto));

        assertThat(thrown.getMessage()).isEqualTo("Недостаточно средств");
        assertThat(thrown.getStatus()).isEqualTo(HttpStatus.PAYMENT_REQUIRED);

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(accountRepository, times(2)).findByUserId(anyLong());
    }

    @Test
    void transferMoneyToTheSameAccountTest() {
        TransferDto transferDto = TransferDto.builder()
                .toUserEmail("test@email.com")
                .amount(new BigDecimal(5000))
                .build();

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(fromUser));
        when(accountRepository.findByUserId(1L)).thenReturn(Optional.of(fromAccount));

        TransferException thrown = assertThrows(TransferException.class,
                () -> transferService.transferMoney(1L, transferDto));

        assertThat(thrown.getMessage()).isEqualTo("Невозможно совершить перевод на один и тот же аккаунт");
        assertThat(thrown.getStatus()).isEqualTo(HttpStatus.CONFLICT);

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(accountRepository, times(2)).findByUserId(anyLong());
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userRepository, accountRepository);
    }
}