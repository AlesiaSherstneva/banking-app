package com.example.bankingapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthDto {
    @NotBlank(message = "Поле email обязательно для заполнения")
    @Email(message = "Неправильный email")
    private String email;

    @NotBlank(message = "Поле password обязательно для заполнения")
    @Size(min = 8, max = 500)
    private String password;
}
