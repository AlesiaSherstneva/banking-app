package com.example.bankingapp.dto;

import com.example.bankingapp.dto.enums.OperationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class EmailOperation {
    @Email(message = "Неправильный формат существующего email")
    String emailToChange;

    @NotBlank(message = "Поле email обязательно для заполнения")
    @Email(message = "Неправильный формат email")
    String email;

    @NotNull(message = "Вид операции должен быть указан")
    @Pattern(regexp = "ADD|UPDATE|REMOVE",
            message = "Вид операции должен быть ADD, UPDATE или REMOVE")
    String operation;

    public OperationType getOperationType() {
        return OperationType.valueOf(operation.toUpperCase());
    }
}
