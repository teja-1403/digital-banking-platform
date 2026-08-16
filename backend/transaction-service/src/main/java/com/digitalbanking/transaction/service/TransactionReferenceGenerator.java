package com.digitalbanking.transaction.service;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class TransactionReferenceGenerator {

    public String generate() {

        String date = LocalDate.now()
                .toString()
                .replace("-", "");

        String suffix = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();

        return "TXN-" + date + "-" + suffix;
    }
}