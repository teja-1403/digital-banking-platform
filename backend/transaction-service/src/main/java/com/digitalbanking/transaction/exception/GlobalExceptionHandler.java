package com.digitalbanking.transaction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRule(
            BusinessRuleException ex
    ) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "timestamp",
                                LocalDateTime.now(),
                                "status",
                                400,
                                "error",
                                "BUSINESS_RULE_VIOLATION",
                                "message",
                                ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(TransactionProcessingException.class)
    public ResponseEntity<Map<String, Object>> handleProcessingError(
            TransactionProcessingException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                        Map.of(
                                "timestamp",
                                LocalDateTime.now(),
                                "status",
                                503,
                                "error",
                                "TRANSACTION_PROCESSING_ERROR",
                                "message",
                                ex.getMessage()
                        )
                );
    }
}