package com.credibank.cards.exceptions.handler;

import com.credibank.cards.exceptions.dto.GeneralError;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class BankExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GeneralError> handleOneException(final RuntimeException ex, final WebRequest request) {
        logger.error("There is a BankException with code", ex);
        final GeneralError error = GeneralError.builder()
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(error, new HttpHeaders(), error.getStatusCode());
    }

    @Override
    @SneakyThrows
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                body.put(fieldError.getField(), fieldError.getDefaultMessage())
        );

        final GeneralError generalError = GeneralError.builder()
                .statusCode(status.value())
                .message(body.toString())
                .build();

        return new ResponseEntity<>(generalError, headers, status);
    }

    @SneakyThrows
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final GeneralError generalError = GeneralError.builder()
                .statusCode(status.value())
                .message(ex.getCause().getMessage())
                .build();

        return new ResponseEntity<>(generalError, headers, status);
    }
}
