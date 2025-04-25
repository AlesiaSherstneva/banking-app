package com.example.bankingapp.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(Long id) {
        super(String.format("Пользователь с id %d не найден", id), HttpStatus.BAD_REQUEST);
    }

    public UserNotFoundException(String email) {
        super(String.format("Пользователь с email %s не найден", email), HttpStatus.BAD_REQUEST);
    }
}
