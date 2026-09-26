import { beforeEach, describe, expect, it, vi } from "vitest";

import axiosClient from "./axiosClient";

import {
  getAdminAccountStats,
  getAdminTransactionStats,
  getAdminUserStats,
} from "./adminApi";

vi.mock("./axiosClient", () => ({
  default: {
    get: vi.fn(),
  },
}));

describe("adminApi", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("gets admin user stats", async () => {
    vi.mocked(axiosClient.get).mockResolvedValue({
      data: { totalUsers: 10, activeUsers: 8 },
    });

    const result = await getAdminUserStats();

    expect(axiosClient.get).toHaveBeenCalledWith("/api/admin/user-stats");
    expect(result.totalUsers).toBe(10);
  });

  it("gets admin account stats", async () => {
    vi.mocked(axiosClient.get).mockResolvedValue({
      data: { totalAccounts: 25, activeAccounts: 20 },
    });

    const result = await getAdminAccountStats();

    expect(axiosClient.get).toHaveBeenCalledWith("/api/admin/account-stats");
    expect(result.totalAccounts).toBe(25);
  });

  it("gets admin transaction stats", async () => {
    vi.mocked(axiosClient.get).mockResolvedValue({
      data: { totalTransactions: 120, completedTransactions: 99 },
    });

    const result = await getAdminTransactionStats();

    expect(axiosClient.get).toHaveBeenCalledWith(
      "/api/admin/transaction-stats",
    );
    expect(result.totalTransactions).toBe(120);
  });
});
