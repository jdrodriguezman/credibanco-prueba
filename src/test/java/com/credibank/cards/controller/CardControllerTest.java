package com.credibank.cards.controller;

import com.credibank.cards.domain.dto.RechargeRequest;
import com.credibank.cards.domain.dto.BalanceResponse;
import com.credibank.cards.domain.dto.CardRequest;
import com.credibank.cards.domain.dto.CardResponse;
import com.credibank.cards.service.card.CardServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @InjectMocks
    private CardController cardController;
    @Mock
    private CardServiceImpl cardService;

    @Test
    void generateNumberCard() {
        var cardNumber = "102030123456789";
        when(cardService.generateNumberCard(anyString()))
                .thenReturn(CardResponse.builder()
                        .cardNumber(cardNumber)
                        .build()
                );
        var result = cardController.generateNumberCard("102030");
        Assertions.assertEquals("102030123456789", Objects.requireNonNull(result.getBody()).getCardNumber());
        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void activate() {
        var result = cardController.enrollCard(CardRequest.builder().cardId("102030123456789").build());
        assertEquals(200, result.getStatusCode().value());
        verify(cardService, atLeastOnce()).enrollCard(anyString());
    }

    @Test
    void block() {
        var result = cardController.block("102030123456789");
        assertEquals(200, result.getStatusCode().value());
        verify(cardService, atLeastOnce()).block(anyString());
    }

    @Test
    void recharge() {
        var result = cardController.recharge(RechargeRequest.builder()
                .cardId("102030123456789")
                .balance(new BigDecimal(100))
                .build()
        );
        assertEquals(200, result.getStatusCode().value());
        verify(cardService, atLeastOnce()).recharge(any(RechargeRequest.class));
    }

    @Test
    void getBalance() {
        var cardBalanceResponse = BalanceResponse.builder()
                .cardId("102030123456789")
                .balance(100)
                .build();
        when(cardService.getBalance(anyString())).thenReturn(cardBalanceResponse);
        var result = cardController.getBalance("102030123456789");
        assertEquals(200, result.getStatusCode().value());
        Assertions.assertEquals(cardBalanceResponse, result.getBody());
    }
}