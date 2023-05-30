package com.credibank.cards.exceptions.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeneralError {
    int statusCode;
    String message;
}
