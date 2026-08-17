package com.digitalbanking.auth.dto;

public class AdminUserStatsResponse {

    private long totalUsers;

    public AdminUserStatsResponse() {
    }

    public AdminUserStatsResponse(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalUsers() {
        return totalUsers;
    }
}