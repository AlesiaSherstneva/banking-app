package com.example.bankingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDto {
    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private Set<String> emails;
    private Set<String> phones;
    private BigDecimal balance;
}
