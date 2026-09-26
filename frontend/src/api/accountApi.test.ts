import { beforeEach, describe, expect, it, vi } from "vitest";

import axiosClient from "./axiosClient";

import {
  activateAccount,
  closeAccount,
  createAccount,
  fundAccount,
  freezeAccount,
  getAccount,
  getAccounts,
  getFundingStatus,
} from "./accountApi";

vi.mock("./axiosClient", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

describe("accountApi", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("fetches all accounts", async () => {
    vi.mocked(axiosClient.get).mockResolvedValue({
      data: [
        {
          id: 1,
          accountNumber: "111111111111",
          accountType: "SAVINGS",
          balance: 2000,
          currency: "INR",
          status: "ACTIVE",
        },
      ],
    });

    const result = await getAccounts();

    expect(axiosClient.get).toHaveBeenCalledWith("/api/accounts");
    expect(result[0].accountType).toBe("SAVINGS");
  });

  it("fetches a single account by id", async () => {
    vi.mocked(axiosClient.get).mockResolvedValue({
      data: {
        id: 1,
        accountNumber: "111111111111",
        accountType: "CURRENT",
        balance: 5000,
        currency: "INR",
        status: "ACTIVE",
      },
    });

    const result = await getAccount(1);

    expect(axiosClient.get).toHaveBeenCalledWith("/api/accounts/1");
    expect(result.accountNumber).toBe("111111111111");
  });

  it("creates a new account with the correct payload", async () => {
    vi.mocked(axiosClient.post).mockResolvedValue({
      data: {
        id: 2,
        accountNumber: "222222222222",
        accountType: "SAVINGS",
        balance: 0,
        currency: "INR",
        status: "ACTIVE",
      },
    });

    const result = await createAccount("SAVINGS");

    expect(axiosClient.post).toHaveBeenCalledWith("/api/accounts", {
      accountType: "SAVINGS",
    });
    expect(result.accountType).toBe("SAVINGS");
  });

  it("funds an account with the provided amount", async () => {
    vi.mocked(axiosClient.post).mockResolvedValue({
      data: {
        fundingReference: "FUND-1",
        accountId: 1,
        accountNumber: "111111111111",
        amount: 250,
        balance: 1250,
        currency: "INR",
      },
    });

    const result = await fundAccount(1, 250);

    expect(axiosClient.post).toHaveBeenCalledWith("/api/accounts/1/fund", {
      amount: 250,
    });
    expect(result.balance).toBe(1250);
  });

  it("checks the funding status endpoint", async () => {
    vi.mocked(axiosClient.get).mockResolvedValue({ data: true });

    const result = await getFundingStatus();

    expect(axiosClient.get).toHaveBeenCalledWith(
      "/api/accounts/funding-status",
    );
    expect(result).toBe(true);
  });

  it("freezes an account", async () => {
    vi.mocked(axiosClient.post).mockResolvedValue({
      data: {
        id: 1,
        accountNumber: "111111111111",
        accountType: "SAVINGS",
        balance: 1000,
        currency: "INR",
        status: "BLOCKED",
      },
    });

    const result = await freezeAccount(1);

    expect(axiosClient.post).toHaveBeenCalledWith("/api/accounts/1/freeze");
    expect(result.status).toBe("BLOCKED");
  });

  it("activates an account", async () => {
    vi.mocked(axiosClient.post).mockResolvedValue({
      data: {
        id: 1,
        accountNumber: "111111111111",
        accountType: "SAVINGS",
        balance: 1000,
        currency: "INR",
        status: "ACTIVE",
      },
    });

    const result = await activateAccount(1);

    expect(axiosClient.post).toHaveBeenCalledWith("/api/accounts/1/activate");
    expect(result.status).toBe("ACTIVE");
  });

  it("closes an account", async () => {
    vi.mocked(axiosClient.post).mockResolvedValue({
      data: {
        id: 1,
        accountNumber: "111111111111",
        accountType: "SAVINGS",
        balance: 0,
        currency: "INR",
        status: "CLOSED",
      },
    });

    const result = await closeAccount(1);

    expect(axiosClient.post).toHaveBeenCalledWith("/api/accounts/1/close");
    expect(result.status).toBe("CLOSED");
  });
});
