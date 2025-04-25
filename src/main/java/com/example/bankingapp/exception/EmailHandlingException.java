package com.example.bankingapp.exception;

import org.springframework.http.HttpStatus;

public class EmailHandlingException extends BaseException {
    public EmailHandlingException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
