package com.example.bankingapp.controller;

import com.example.bankingapp.dto.AuthDto;
import com.example.bankingapp.model.User;
import com.example.bankingapp.security.JwtProvider;
import com.example.bankingapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping
    public ResponseEntity<Object> login(@Valid @RequestBody AuthDto authDto) {
        log.info("Login attempt for email: {}", authDto.getEmail());
        User user = userService.authenticateUser(authDto);

        return ResponseEntity.ok(jwtProvider.generateToken(user.getId()));
    }
}
