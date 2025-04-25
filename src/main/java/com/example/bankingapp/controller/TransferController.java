package com.example.bankingapp.controller;

import com.example.bankingapp.dto.TransferDto;
import com.example.bankingapp.security.JwtProvider;
import com.example.bankingapp.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;
    private final JwtProvider jwtProvider;

    @PostMapping
    public ResponseEntity<Void> transferMoney(@RequestHeader("Authorization") String authHeader,
                                              @Valid @RequestBody TransferDto transferDto) {
        Long userId = extractIdFromToken(authHeader);

        transferService.transferMoney(userId, transferDto);
        return ResponseEntity.ok().build();
    }

    private Long extractIdFromToken(String token) {
        String jwt = token.substring(7);
        return jwtProvider.getUserIdFromToken(jwt);
    }
}
