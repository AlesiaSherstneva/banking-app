package com.example.bankingapp.service;

import com.example.bankingapp.dto.PhoneOperation;

public interface PhoneDataService {
    void handlePhoneOperation(Long userId, PhoneOperation operation);
}
