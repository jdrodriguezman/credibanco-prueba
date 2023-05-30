package com.credibank.cards.controller;

import com.credibank.cards.domain.dto.RechargeRequest;
import com.credibank.cards.domain.dto.BalanceResponse;
import com.credibank.cards.domain.dto.CardRequest;
import com.credibank.cards.domain.dto.CardResponse;
import com.credibank.cards.service.card.CardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/card")
@Api(tags = "Card")
public class CardController {

    private final CardService cardService;

    @GetMapping("/{productId}/number")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Ok"),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request"),
                    @ApiResponse(
                            code = 409,
                            message = "Conflict error"),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error")
            })
    @ApiOperation(value = "Generate number card", notes = "Generate a new card number from product id")
    public ResponseEntity<CardResponse> generateNumberCard(@PathVariable String productId) {
        return ResponseEntity.ok(cardService.generateNumberCard(productId));
    }

    @PostMapping("/enroll")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Ok"),
    @ApiResponse(
            code = 400,
            message = "Bad Request"),
    @ApiResponse(
            code = 409,
            message = "Conflict error"),
    @ApiResponse(
            code = 500,
            message = "Internal Server Error")
})
    @ApiOperation(value = "Enroll card", notes = "Enroll a card created")
    public ResponseEntity<Void> enrollCard(@RequestBody CardRequest request) {
        cardService.enrollCard(request.getCardId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cardId}")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Ok"),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request"),
                    @ApiResponse(
                            code = 409,
                            message = "Conflict error"),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error")
            })
    @ApiOperation(value = "Block card", notes = "Block by card id")
    public ResponseEntity<Void> block(@PathVariable String cardId) {
        cardService.block(cardId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/balance")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Ok"),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request"),
                    @ApiResponse(
                            code = 409,
                            message = "Conflict error"),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error")
            })
    @ApiOperation(value = "Recharge", notes = "Increase balance by card")
    public ResponseEntity<Void> recharge(@RequestBody @Valid RechargeRequest request) {
        cardService.recharge(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/balance/{cardId}")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Ok"),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request"),
                    @ApiResponse(
                            code = 409,
                            message = "Conflict error"),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error")
            })
    @ApiOperation(value = "Get balance", notes = "Get a balance by card id")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable String cardId) {
        return ResponseEntity.ok(cardService.getBalance(cardId));
    }
}
