package com.credibank.cards.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardResponse {

    private String cardNumber;
}
