package com.credibank.cards.service.transaction;

import com.credibank.cards.domain.dto.AnnulationRequest;
import com.credibank.cards.domain.dto.PurchaseRequest;
import com.credibank.cards.domain.dto.PurchaseResponse;
import com.credibank.cards.domain.dto.TransactionResponse;


public interface TransactionService {
    PurchaseResponse purchase(PurchaseRequest request);
    TransactionResponse getTransaction(Long transactionId);
    void annulation(AnnulationRequest request);
}