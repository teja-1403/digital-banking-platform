package com.digitalbanking.account.dto;

import java.time.LocalDateTime;

public class BeneficiaryResponse {

    private Long id;
    private String beneficiaryAccountNumber;
    private String nickname;
    private LocalDateTime createdAt;

    public BeneficiaryResponse() {
    }

    public BeneficiaryResponse(
            Long id,
            String beneficiaryAccountNumber,
            String nickname,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
        this.nickname = nickname;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public String getNickname() {
        return nickname;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}