package com.example.bankingapp.controller;

import com.example.bankingapp.dto.UserDto;
import com.example.bankingapp.dto.UserFilter;
import com.example.bankingapp.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam(required = false)
            @Size(max = 500, message = "Имя не должно быть длиннее 500 символов")
            String name,

            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd.MM.yyyy")
            @Past(message = "Дата рождения должна раньше текущей даты")
            LocalDate dateOfBirth,

            @RequestParam(required = false)
            @Email(message = "Неправильный email")
            String email,

            @RequestParam(required = false)
            @Pattern(regexp = "\\d{11}", message = "Номер телефона должен содержать 11 цифр")
            String phone,

            @RequestParam(required = false, defaultValue = "0")
            @Min(value = 0, message = "Номер страницы не может иметь отрицательное значение")
            int page,

            @RequestParam(required = false, defaultValue = "10")
            @Min(value = 1, message = "Количество результатов на странице должно быть положительным значением")
            @Max(value = 20, message = "Количество результатов на странице не может быть больше 20")
            int size) {

        UserFilter filter = UserFilter.builder()
                .name(name)
                .dateOfBirth(dateOfBirth)
                .email(email)
                .phone(phone)
                .build();

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.searchUsers(filter, pageable));
    }
}
