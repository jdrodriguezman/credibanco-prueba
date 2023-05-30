package com.credibank.cards.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
public class PurchaseResponse {

    @ApiModelProperty(example = "1020301234567890")
    private String transactionId;

    @NotBlank
    @Positive
    @ApiModelProperty(example = "100")
    private LocalDateTime transactionDate;

}

