package com.example.bankingapp.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BaseException {
    public InvalidCredentialsException() {
        super("Неверный email или пароль", HttpStatus.BAD_REQUEST);
    }
}
