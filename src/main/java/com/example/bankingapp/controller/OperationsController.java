package com.example.bankingapp.controller;

import com.example.bankingapp.dto.EmailOperation;
import com.example.bankingapp.dto.PhoneOperation;
import com.example.bankingapp.dto.UserAuthDto;
import com.example.bankingapp.security.JwtProvider;
import com.example.bankingapp.service.EmailDataService;
import com.example.bankingapp.service.PhoneDataService;
import com.example.bankingapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@Validated
public class OperationsController {
    private final UserService userService;
    private final EmailDataService emailDataService;
    private final PhoneDataService phoneDataService;
    private final JwtProvider jwtProvider;

    @GetMapping
    public ResponseEntity<UserAuthDto> getMyProfile(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(userService.getFullUserById(extractIdFromToken(authHeader)));
    }

    @PatchMapping("/emails")
    public ResponseEntity<Void> updateEmails(@RequestHeader("Authorization") String authHeader,
                                             @Valid @RequestBody EmailOperation operation) {
        Long userId = extractIdFromToken(authHeader);
        emailDataService.handleEmailOperation(userId, operation);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/phones")
    public ResponseEntity<Void> updatePhones(@RequestHeader("Authorization") String authHeader,
                                             @Valid @RequestBody PhoneOperation operation) {
        Long userId = extractIdFromToken(authHeader);
        phoneDataService.handlePhoneOperation(userId, operation);

        return ResponseEntity.ok().build();
    }

    private Long extractIdFromToken(String token) {
        String jwt = token.substring(7);
        return jwtProvider.getUserIdFromToken(jwt);
    }
}
