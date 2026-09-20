package com.digitalbanking.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class GatewayFallbackController {

    @RequestMapping("/fallback/service-unavailable")
    public ResponseEntity<Map<String, Object>> serviceUnavailable() {

        Map<String, Object> response = Map.of(
                "code", "SERVICE_UNAVAILABLE",
                "message", "The requested service is temporarily unavailable. Please try again later.",
                "timestamp", Instant.now().toString()
        );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}