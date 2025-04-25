package com.example.bankingapp.exception;

import org.springframework.http.HttpStatus;

public class PhoneHandlingException extends BaseException {
    public PhoneHandlingException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
