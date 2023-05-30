package com.credibank.cards.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BalanceResponse {

    @ApiModelProperty(example = "1020301234567890")
    private String cardId;

    @ApiModelProperty(example = "100")
    private double balance;
}
