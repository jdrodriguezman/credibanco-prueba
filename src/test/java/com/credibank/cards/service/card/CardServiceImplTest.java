package com.credibank.cards.service.card;

import com.credibank.cards.domain.dto.RechargeRequest;
import com.credibank.cards.domain.dto.BalanceResponse;
import com.credibank.cards.domain.entity.Card;
import com.credibank.cards.exceptions.NotFoundException;
import com.credibank.cards.repository.CardRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;
    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void generateNumberCard() {
        var productId = "102030";
        var result = cardService.generateNumberCard(productId);
        Assertions.assertEquals(16, result.getCardNumber().length());
        verify(cardRepository, atLeastOnce()).save(any(Card.class));
    }

    @Test
    void activate() {
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(Boolean.TRUE);
        cardService.enrollCard("1020301234567890");
        verify(cardRepository, atLeastOnce()).existsByCardNumber(anyString());
        verify(cardRepository, atLeastOnce()).enrollCard(anyString());
    }

    @Test
    void activateThrowErrorWhenCardNotExist() {
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(Boolean.FALSE);
        Exception ex = assertThrows(
                NotFoundException.class,
                () -> cardService.enrollCard("1020301234567890")
        );
        assertEquals("CARD NOT FOUND", ex.getMessage());
        verify(cardRepository, atLeastOnce()).existsByCardNumber(anyString());
    }

    @Test
    void block() {
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(Boolean.TRUE);
        cardService.block("1020301234567890");
        verify(cardRepository, atLeastOnce()).existsByCardNumber(anyString());
        verify(cardRepository, atLeastOnce()).blockCard(anyString());
    }

    @Test
    void blockThrowErrorWhenCardNotExist() {
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(Boolean.FALSE);
        Exception ex = assertThrows(
                NotFoundException.class,
                () -> cardService.block("1020301234567890")
        );
        assertEquals("CARD NOT FOUND", ex.getMessage());
        verify(cardRepository, atLeastOnce()).existsByCardNumber(anyString());
    }

    @Test
    void rechargeBalance() {
        var request = RechargeRequest.builder()
                .cardId("1020301234567890")
                .balance(new BigDecimal(100))
                .build();
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(Boolean.TRUE);
        cardService.recharge(request);
        verify(cardRepository, atLeastOnce()).existsByCardNumber(anyString());
        verify(cardRepository, atLeastOnce()).rechargeBalance(anyString(), any());
    }

    @Test
    void rechargeBalanceThrowErrorWhenCardNotExist() {
        var request = RechargeRequest.builder()
                .cardId("1020301234567890")
                .balance(new BigDecimal(100))
                .build();
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(Boolean.FALSE);
        Exception ex = assertThrows(
                NotFoundException.class,
                () -> cardService.recharge(request)
        );
        assertEquals("CARD NOT FOUND", ex.getMessage());
        verify(cardRepository, atLeastOnce()).existsByCardNumber(anyString());
    }

    @Test
    void getCardBalance() {
        var cardId = "1020301234567890";
        var expectValue = BalanceResponse.builder()
                .cardId(cardId)
                .balance(100)
                .build();
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(Boolean.TRUE);
        when(cardRepository.getBalance(anyString())).thenReturn(100.0);
        var balance = cardService.getBalance(cardId);
        Assertions.assertEquals(expectValue, balance);
        verify(cardRepository, atLeastOnce()).existsByCardNumber(anyString());
        verify(cardRepository, atLeastOnce()).getBalance(anyString());
    }

    @Test
    void getCardBalanceThrowErrorWhenCardNotExist() {
        var cardId = "1020301234567890";
        when(cardRepository.existsByCardNumber(anyString())).thenReturn(Boolean.FALSE);
        Exception ex = assertThrows(
                NotFoundException.class,
                () -> cardService.getBalance(cardId)
        );
        assertEquals("CARD NOT FOUND", ex.getMessage());
        verify(cardRepository, atLeastOnce()).existsByCardNumber(anyString());
    }
}