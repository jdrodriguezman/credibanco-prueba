package com.credibank.cards.service.card;

import com.credibank.cards.domain.dto.RechargeRequest;
import com.credibank.cards.domain.dto.BalanceResponse;
import com.credibank.cards.domain.dto.CardResponse;

public interface CardService {

    CardResponse generateNumberCard(String productId);
    BalanceResponse getBalance(String cardId);
    void recharge(RechargeRequest request);
    void enrollCard(String cardId);
    void block(String cardId);

}

