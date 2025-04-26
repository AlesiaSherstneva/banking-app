package com.example.bankingapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferDto {
    @NotNull(message = "Email получателя перевода должен быть указан")
    @Email(message = "Email получателя перевода должен быть валидным")
    private String toUserEmail;

    @NotNull
    @DecimalMin(value = "0.01", message = "Сумма должна иметь положительное значение")
    private BigDecimal amount;
}
