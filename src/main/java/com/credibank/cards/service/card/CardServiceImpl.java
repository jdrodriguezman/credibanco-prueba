package com.credibank.cards.service.card;

import com.credibank.cards.domain.dto.RechargeRequest;
import com.credibank.cards.domain.dto.BalanceResponse;
import com.credibank.cards.domain.dto.CardResponse;
import com.credibank.cards.domain.entity.Card;
import com.credibank.cards.exceptions.NotFoundException;
import com.credibank.cards.repository.CardRepository;
import com.credibank.cards.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static com.credibank.cards.utils.Constants.RANDOM_NUMBER_LENGTH;
import static com.credibank.cards.utils.Constants.YEARS_CARD_VALIDITY;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Override
    public CardResponse generateNumberCard(String productId) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < RANDOM_NUMBER_LENGTH; i++) {
            stringBuilder.append(secureRandom.nextInt(RANDOM_NUMBER_LENGTH));
        }
        String numberCard = productId.concat(stringBuilder.toString());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        LocalDate expirationDate = YearMonth.now().atEndOfMonth().plusYears(YEARS_CARD_VALIDITY);

        Card card = Card.builder()
                .productId(productId)
                .cardNumber(numberCard)
                .expirationDate(expirationDate)
                .validThru(expirationDate.format(formatter))
                .balance(new BigDecimal(0))
                .build();
        cardRepository.save(card);

        return CardResponse.builder()
                .cardNumber(numberCard)
                .build();
    }

    @Override
    @Transactional
    public void enrollCard(String cardId) {
        if (!cardRepository.existsByCardNumber(cardId)) throw new NotFoundException(Constants.CARD_NOT_FOUND);
        cardRepository.enrollCard(cardId);
    }

    @Override
    @Transactional
    public void block(String cardId) {
        if (!cardRepository.existsByCardNumber(cardId)) throw new NotFoundException(Constants.CARD_NOT_FOUND);
        cardRepository.blockCard(cardId);
    }

    @Override
    @Transactional
    public void recharge(RechargeRequest request) {
        String cardId = request.getCardId();
        BigDecimal balance = request.getBalance();
        if (!cardRepository.existsByCardNumber(cardId)) throw new NotFoundException(Constants.CARD_NOT_FOUND);
        cardRepository.rechargeBalance(cardId, balance);
    }

    @Override
    public BalanceResponse getBalance(String cardId) {
        if (!cardRepository.existsByCardNumber(cardId)) throw new NotFoundException(Constants.CARD_NOT_FOUND);

        return BalanceResponse.builder()
                .cardId(cardId)
                .balance(cardRepository.getBalance(cardId))
                .build();
    }

}

