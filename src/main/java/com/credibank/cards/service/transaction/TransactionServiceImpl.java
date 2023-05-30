package com.credibank.cards.service.transaction;

import com.credibank.cards.domain.dto.AnnulationRequest;
import com.credibank.cards.domain.dto.PurchaseRequest;
import com.credibank.cards.domain.dto.PurchaseResponse;
import com.credibank.cards.domain.dto.TransactionResponse;
import com.credibank.cards.domain.entity.Transaction;
import com.credibank.cards.exceptions.ConflictException;
import com.credibank.cards.exceptions.NotFoundException;
import com.credibank.cards.repository.CardRepository;
import com.credibank.cards.repository.TransactionRepository;
import com.credibank.cards.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public PurchaseResponse purchase(PurchaseRequest request) {
        String cardId = request.getCardId();
        BigDecimal price = request.getPrice();
        var card = cardRepository.findByCardNumber(cardId)
                .orElseThrow(() -> new NotFoundException(Constants.CARD_NOT_FOUND));

        if (card.getBalance().doubleValue() < price.doubleValue()) {
            throw new ConflictException(Constants.INSUFFICIENT_BALANCE);
        }
        LocalDate expirationDate = card.getExpirationDate();
        if (LocalDate.now().isAfter(expirationDate)) throw new ConflictException(Constants.EXPIRED_CARD);
        if (!card.isEnroll()) throw new ConflictException(Constants.INACTIVE_CARD);
        if (card.isBlock()) throw new ConflictException(Constants.LOCKED_CARD);

        cardRepository.deductBalance(card.getCardNumber(), price);

        var transaction = Transaction.builder()
                .card(card)
                .price(price)
                .transactionDate(LocalDateTime.now())
                .build();

        var transactionSaved = transactionRepository.save(transaction);

        return PurchaseResponse.builder()
                .transactionId(transactionSaved.getId().toString())
                .transactionDate(transactionSaved.getTransactionDate())
                .build();
    }

    @Override
    public TransactionResponse getTransaction(Long transactionId) {
        var transaction = transactionRepository.findById(transactionId).orElseThrow(() ->
                new NotFoundException(Constants.TRANSACTION_NOT_FOUND));

        return TransactionResponse.builder()
                .id(transaction.getId())
                .cardNumber(transaction.getCard().getCardNumber())
                .price(transaction.getPrice())
                .transactionDate(transaction.getTransactionDate())
                .isCancelled(transaction.isCancel())
                .build();
    }

    @Override
    @Transactional
    public void annulation(AnnulationRequest request) {
        Long transactionId = Long.valueOf(request.getTransactionId());
        var transaction = transactionRepository.findById(transactionId).orElseThrow(() ->
                new NotFoundException(Constants.TRANSACTION_NOT_FOUND));

        long diffHours = ChronoUnit.HOURS.between(transaction.getTransactionDate(), LocalDateTime.now());
        if (diffHours > Constants.ANNULATION_LIMIT_HOURS)
            throw new ConflictException(Constants.ANNULATION_TIME_EXCEEDED);
        transactionRepository.cancel(transactionId);
        cardRepository.rechargeBalance(transaction.getCard().getCardNumber(), transaction.getPrice());
    }
}
