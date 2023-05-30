package com.credibank.cards.service.transaction;

import com.credibank.cards.domain.dto.AnnulationRequest;
import com.credibank.cards.domain.dto.PurchaseRequest;
import com.credibank.cards.domain.entity.Card;
import com.credibank.cards.domain.entity.Transaction;
import com.credibank.cards.exceptions.ConflictException;
import com.credibank.cards.exceptions.NotFoundException;
import com.credibank.cards.repository.CardRepository;
import com.credibank.cards.repository.TransactionRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CardRepository cardRepository;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    private static MockedStatic<LocalDateTime> mockedStatic;

    @BeforeAll
    public static void init() {
        mockedStatic = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
    }

    @AfterAll
    public static void close() {
        mockedStatic.close();
    }

    @Test
    void doPurchase() {
        var purchaseRequest = PurchaseRequest.builder()
                .cardId("1020301234567890")
                .price(new BigDecimal(100))
                .build();
        var card = buildCard(true, false, new BigDecimal(200));
        var transaction = buildTransaction(card);
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(card));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        var result = transactionService.purchase(purchaseRequest);
        Assertions.assertEquals(transaction.getId().toString(), result.getTransactionId());
        verify(cardRepository, atLeastOnce()).deductBalance(anyString(), any(BigDecimal.class));
    }

    @Test
    void doPurchaseThrowErrorWhenCardIsNotFound() {
        var purchaseRequest = PurchaseRequest.builder()
                .cardId("1020301234567890")
                .price(new BigDecimal(100))
                .build();
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());
        Exception ex = assertThrows(
                NotFoundException.class,
                () -> transactionService.purchase(purchaseRequest)
        );
        assertEquals("CARD NOT FOUND", ex.getMessage());
        verify(cardRepository, atLeastOnce()).findByCardNumber(anyString());
    }

    @Test
    void doPurchaseThrowErrorWhenBalanceIsInsufficient() {
        var purchaseRequest = PurchaseRequest.builder()
                .cardId("1020301234567890")
                .price(new BigDecimal(300))
                .build();
        var card = buildCard(true, false, new BigDecimal(200));
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(card));
        Exception ex = assertThrows(
                ConflictException.class,
                () -> transactionService.purchase(purchaseRequest)
        );
        assertEquals("INSUFFICIENT BALANCE", ex.getMessage());
        verify(cardRepository, atLeastOnce()).findByCardNumber(anyString());
    }

    @Test
    void doPurchaseThrowErrorWhenCardIsExpired() {
        var purchaseRequest = PurchaseRequest.builder()
                .cardId("1020301234567890")
                .price(new BigDecimal(100))
                .build();
        var card = buildCard(true, false, new BigDecimal(200));
        card.setExpirationDate(LocalDate.of(2022,10,22));
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(card));
        Exception ex = assertThrows(
                ConflictException.class,
                () -> transactionService.purchase(purchaseRequest)
        );
        assertEquals("EXPIRED CARD", ex.getMessage());
        verify(cardRepository, atLeastOnce()).findByCardNumber(anyString());
    }

    @Test
    void doPurchaseThrowErrorWhenCardIsInactive() {
        var purchaseRequest = PurchaseRequest.builder()
                .cardId("1020301234567890")
                .price(new BigDecimal(100))
                .build();
        var card = buildCard(false, false, new BigDecimal(200));
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(card));
        Exception ex = assertThrows(
                ConflictException.class,
                () -> transactionService.purchase(purchaseRequest)
        );
        assertEquals("INACTIVE CARD", ex.getMessage());
        verify(cardRepository, atLeastOnce()).findByCardNumber(anyString());
    }

    @Test
    void doPurchaseThrowErrorWhenCardIsLocked() {
        var purchaseRequest = PurchaseRequest.builder()
                .cardId("1020301234567890")
                .price(new BigDecimal(100))
                .build();
        var card = buildCard(true, true, new BigDecimal(200));
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(card));
        Exception ex = assertThrows(
                ConflictException.class,
                () -> transactionService.purchase(purchaseRequest)
        );
        assertEquals("LOCKED CARD", ex.getMessage());
        verify(cardRepository, atLeastOnce()).findByCardNumber(anyString());
    }

    @Test
    void doAnnulation() {
        var request = AnnulationRequest.builder()
                .transactionId("10000")
                .cardId("1020301234567890")
                .build();
        var transaction = Optional.of(buildTransaction());
        when(transactionRepository.findById(anyLong())).thenReturn(transaction);

        LocalDateTime date = LocalDateTime.of(2023, 5, 28, 16, 20);
        mockedStatic.when(LocalDateTime::now).thenReturn(date);

        transactionService.annulation(request);

        verify(transactionRepository, atLeastOnce()).cancel(anyLong());
        verify(cardRepository, atLeastOnce()).rechargeBalance(anyString(), any(BigDecimal.class));
    }

    @Test
    void doAnnulationThrowErrorWhenTransactionIsNotFound() {
        var request = AnnulationRequest.builder()
                .transactionId("10000")
                .cardId("1020301234567890")
                .build();
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class, () -> transactionService.annulation(request));

        assertEquals("TRANSACTION NOT FOUND", ex.getMessage());
    }

    @Test
    void getTransaction() {
        var transaction = buildTransaction();
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(transaction));
        var result = transactionService.getTransaction(1L);
        assertNotNull(result);
    }

    @Test
    void getTransactionThrowErrorWhenTransactionIsNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception ex = assertThrows(NotFoundException.class, () -> transactionService.getTransaction(1L));

        assertEquals("TRANSACTION NOT FOUND", ex.getMessage());
    }


    @Test
    void doAnnulationThrowErrorWhenTimeToAnnulationIsExceded() {
        var request = AnnulationRequest.builder()
                .transactionId("10000")
                .cardId("1020301234567890")
                .build();
        var transaction = Optional.of(buildTransaction());
        when(transactionRepository.findById(anyLong())).thenReturn(transaction);

        LocalDateTime date = LocalDateTime.of(2023, 5, 29, 13, 20);
        mockedStatic.when(LocalDateTime::now).thenReturn(date);

        Exception ex = assertThrows(ConflictException.class, () -> transactionService.annulation(request));

        assertEquals("ANNULATION TIME EXCEEDED", ex.getMessage());
    }

    private Card buildCard(boolean isActive, boolean isBlocked, BigDecimal balance) {
        return Card.builder()
                .productId("102030")
                .cardNumber("1020301234567890")
                .cardHolder("John Doe")
                .expirationDate(LocalDate.of(2026,10,10))
                .enroll(isActive)
                .block(isBlocked)
                .balance(balance)
                .build();
    }

    private Transaction buildTransaction(Card card) {
        return Transaction.builder()
                .id(10000L)
                .card(card)
                .price(new BigDecimal(100))
                .transactionDate(LocalDateTime.of(2023, 5, 28, 16, 20))
                .build();
    }

    private Transaction buildTransaction() {
        return Transaction.builder()
                .id(10000L)
                .card(buildCard(true, false, new BigDecimal(200)))
                .price(new BigDecimal(100))
                .transactionDate(LocalDateTime.of(2023, 5, 28, 12, 0))
                .build();
    }
}