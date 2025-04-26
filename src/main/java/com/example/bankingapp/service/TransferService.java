package com.example.bankingapp.service;

import com.example.bankingapp.dto.TransferDto;

public interface TransferService {
    void transferMoney(Long fromUserId, TransferDto transferDto);
}
