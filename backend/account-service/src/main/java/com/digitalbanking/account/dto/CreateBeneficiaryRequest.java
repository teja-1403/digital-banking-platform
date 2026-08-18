package com.digitalbanking.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateBeneficiaryRequest {

    @NotBlank(message = "Beneficiary account number is required")
    @Size(max = 20, message = "Beneficiary account number cannot exceed 20 characters")
    private String beneficiaryAccountNumber;

    @Size(max = 50, message = "Nickname cannot exceed 50 characters")
    private String nickname;

    public CreateBeneficiaryRequest() {
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}