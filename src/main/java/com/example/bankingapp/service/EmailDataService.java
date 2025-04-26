package com.example.bankingapp.service;

import com.example.bankingapp.dto.EmailOperation;

public interface EmailDataService {
    void handleEmailOperation(Long userId, EmailOperation operation);
}
