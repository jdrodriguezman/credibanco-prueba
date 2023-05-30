package com.credibank.cards.controller;

import com.credibank.cards.domain.dto.AnnulationRequest;
import com.credibank.cards.domain.dto.PurchaseRequest;
import com.credibank.cards.domain.dto.PurchaseResponse;
import com.credibank.cards.domain.dto.TransactionResponse;
import com.credibank.cards.service.transaction.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/transaction")
@Api(tags = "Transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/purchase")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Ok"),
                    @ApiResponse(
                            code = 400,
                            message = "HTTP Bad Request"),
                    @ApiResponse(
                            code = 409,
                            message = "Conflict error"),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error")
            })
    @ApiOperation(value = "Purchase", notes = "Allow do a transaction")
    public ResponseEntity<PurchaseResponse> purchase(@RequestBody PurchaseRequest request) {
        return ResponseEntity.ok(transactionService.purchase(request));
    }

    @GetMapping("/{transactionId}")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Ok"),
                    @ApiResponse(
                            code = 400,
                            message = "HTTP Bad Request"),
                    @ApiResponse(
                            code = 409,
                            message = "Conflict error"),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error")
            })
    @ApiOperation(value = "Get transaction", notes = "Get transaction by id")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(transactionId));
    }

    @PostMapping("/annulation")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Ok"),
                    @ApiResponse(
                            code = 400,
                            message = "HTTP Bad Request"),
                    @ApiResponse(
                            code = 409,
                            message = "Conflict error"),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error")
            })
    @ApiOperation(value = "Annulation", notes = "Allow do annulation transaction")
    public ResponseEntity<PurchaseResponse> annulation(@RequestBody AnnulationRequest request) {
        transactionService.annulation(request);
        return ResponseEntity.ok().build();
    }
}
