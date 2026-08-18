package com.digitalbanking.account.dto;

public class AccountOwnershipResponse {

    private Long accountId;
    private boolean owner;

    public AccountOwnershipResponse() {
    }

    public AccountOwnershipResponse(
            Long accountId,
            boolean owner
    ) {
        this.accountId = accountId;
        this.owner = owner;
    }

    public Long getAccountId() {
        return accountId;
    }

    public boolean isOwner() {
        return owner;
    }
}