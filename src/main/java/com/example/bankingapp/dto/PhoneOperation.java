package com.example.bankingapp.dto;

import com.example.bankingapp.dto.enums.OperationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PhoneOperation {
    @Pattern(regexp = "\\d{11}", message = "Существующий номер телефона должен содержать 11 цифр")
    String phoneToChange;

    @NotBlank(message = "Номер телефона обязателен для заполнения")
    @Pattern(regexp = "\\d{11}", message = "Новый номер телефона должен содержать 11 цифр")
    String phone;

    @NotNull(message = "Вид операции должен быть указан")
    @Pattern(regexp = "ADD|UPDATE|REMOVE",
            message = "Вид операции должен быть ADD, UPDATE или REMOVE")
    String operation;

    public OperationType getOperationType() {
        return OperationType.valueOf(operation.toUpperCase());
    }
}
