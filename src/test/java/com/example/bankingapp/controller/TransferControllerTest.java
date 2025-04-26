package com.example.bankingapp.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class TransferControllerTest extends BaseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final String testToken = "Bearer " + generateTestToken();

    @Test
    @WithMockUser(username = "ivanov@example.com")
    void transferMoneySuccessTest() throws Exception {
        String userJson = "{\"toUserEmail\": \"sidorov@example.com\", \"amount\": 100}";

        mockMvc.perform(post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .header("Authorization", testToken))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "ivanov@example.com")
    void transferMoneyIncorrectRecipient() throws Exception {
        String userJson = "{\"toUserEmail\": \"test@example.com\", \"amount\": 100}";

        mockMvc.perform(post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .header("Authorization", testToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пользователь с email test@example.com не найден"))
                .andExpect(jsonPath("$.status").value(400));
    }


    @Test
    @WithMockUser(username = "ivanov@example.com")
    void transferMoneyLittleBalance() throws Exception {
        String userJson = "{\"toUserEmail\": \"petrova@example.com\", \"amount\": 1000000}";

        mockMvc.perform(post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .header("Authorization", testToken))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.message").value("Недостаточно средств"))
                .andExpect(jsonPath("$.status").value(402));
    }

    @Test
    @WithMockUser(username = "ivanov@example.com")
    void transferMoneyNegativeAmount() throws Exception {
        String userJson = "{\"toUserEmail\": \"petrova@example.com\", \"amount\": -1}";

        mockMvc.perform(post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .header("Authorization", testToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("amount - Сумма должна иметь положительное значение"))
                .andExpect(jsonPath("$.status").value(400));
    }

    private String generateTestToken() {
        String secretKey = "myVerySecretKeyLosLessThan32CharactersLong";

        return Jwts.builder()
                .claim("userId", 1L)
                .issuedAt(Date.from(Instant.now()))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }
}