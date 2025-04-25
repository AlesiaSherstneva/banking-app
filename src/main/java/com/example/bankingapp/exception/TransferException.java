package com.example.bankingapp.exception;

import org.springframework.http.HttpStatus;

public class TransferException extends BaseException {
    public TransferException(String message, HttpStatus status) {
        super(message, status);
    }
}
