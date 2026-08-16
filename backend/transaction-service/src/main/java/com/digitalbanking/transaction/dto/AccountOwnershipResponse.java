package com.digitalbanking.transaction.dto;

public class AccountOwnershipResponse {

    private Long accountId;
    private boolean owner;

    public AccountOwnershipResponse() {
    }

    public Long getAccountId() {
        return accountId;
    }

    public boolean isOwner() {
        return owner;
    }
}