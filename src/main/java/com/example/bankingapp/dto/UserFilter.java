package com.example.bankingapp.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserFilter {
    private final String name;
    private final LocalDate dateOfBirth;
    private final String email;
    private final String phone;
}
