package com.credibank.cards.controller;

import com.credibank.cards.domain.dto.AnnulationRequest;
import com.credibank.cards.domain.dto.PurchaseRequest;
import com.credibank.cards.domain.dto.PurchaseResponse;
import com.credibank.cards.domain.dto.TransactionResponse;
import com.credibank.cards.service.transaction.TransactionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;
    @Mock
    private TransactionServiceImpl transactionService;

    @Test
    void doPurchase() {
        var purchaseRequest = PurchaseRequest.builder()
                .cardId("1020301234567890")
                .price(new BigDecimal(100))
                .build();
        var purchaseResponse = PurchaseResponse.builder()
                .transactionId("1000")
                .transactionDate(LocalDateTime.of(2023, 5, 28, 16, 20))
                .build();
        when(transactionService.purchase(any(PurchaseRequest.class))).thenReturn(purchaseResponse);
        var result = transactionController.purchase(purchaseRequest);
        assertEquals(200, result.getStatusCode().value());
        Assertions.assertEquals(purchaseResponse, result.getBody());
        verify(transactionService, atLeastOnce()).purchase(any(PurchaseRequest.class));
    }

    @Test
    void getTransaction() {

        var transactionResponse = TransactionResponse.builder()
                .id(1L)
                .currency("USD")
                .isCancelled(false)
                .transactionDate(LocalDateTime.of(2023, 5, 28, 16, 20))
                .price(new BigDecimal(100))
                .cardNumber("1020301234567890")
                .build();
        when(transactionService.getTransaction(anyLong())).thenReturn(transactionResponse);
        var result = transactionController.getTransaction(1L);
        assertEquals(200, result.getStatusCode().value());
        Assertions.assertEquals(transactionResponse, result.getBody());
        verify(transactionService, atLeastOnce()).getTransaction(anyLong());
    }

    @Test
    void doAnnulation() {
        var annulationRequest = AnnulationRequest.builder()
                .cardId("1020301234567890")
                .transactionId("1000")
                .build();
        var result = transactionController.annulation(annulationRequest);
        assertEquals(200, result.getStatusCode().value());
        verify(transactionService, atLeastOnce()).annulation(any(AnnulationRequest.class));
    }
}