import { beforeEach, describe, expect, it, vi } from "vitest";

import axiosClient from "./axiosClient";

import {
  createTransfer,
  getAccountTransactions,
  getTransactionDetails,
} from "./transactionApi";

vi.mock("./axiosClient", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

describe("transactionApi", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("creates a transfer with the correct payload and idempotency header", async () => {
    vi.mocked(axiosClient.post).mockResolvedValue({
      data: {
        id: 10,
        transactionReference: "TXN-TEST-001",
        idempotencyKey: "test-key",
        type: "TRANSFER",
        status: "COMPLETED",
        sourceAccountId: 1,
        destinationAccountId: 7,
        amount: 100,
        currency: "INR",
        description: "Test transfer",
        createdAt: "2026-08-17T10:00:00",
      },
    });

    const result = await createTransfer(
      {
        sourceAccountId: 1,
        destinationAccountId: 7,
        amount: 100,
        currency: "INR",
        description: "Test transfer",
      },
      "test-key",
    );

    expect(axiosClient.post).toHaveBeenCalledWith(
      "/api/transactions/transfers",
      {
        sourceAccountId: 1,
        destinationAccountId: 7,
        amount: 100,
        currency: "INR",
        description: "Test transfer",
      },
      {
        headers: {
          "Idempotency-Key": "test-key",
        },
      },
    );
    expect(result.transactionReference).toBe("TXN-TEST-001");
  });

  it("fetches all transactions for an account", async () => {
    vi.mocked(axiosClient.get).mockResolvedValue({
      data: [
        {
          id: 10,
          transactionReference: "TXN-TEST-001",
          idempotencyKey: "test-key",
          type: "TRANSFER",
          status: "COMPLETED",
          sourceAccountId: 1,
          destinationAccountId: 7,
          amount: 100,
          currency: "INR",
          description: "Test transfer",
          createdAt: "2026-08-17T10:00:00",
        },
      ],
    });

    const result = await getAccountTransactions(1);

    expect(axiosClient.get).toHaveBeenCalledWith("/api/transactions/account/1");
    expect(result[0].amount).toBe(100);
  });

  it("fetches a single transaction by reference", async () => {
    vi.mocked(axiosClient.get).mockResolvedValue({
      data: {
        id: 10,
        transactionReference: "TXN-TEST-001",
        idempotencyKey: "test-key",
        type: "TRANSFER",
        status: "COMPLETED",
        sourceAccountId: 1,
        destinationAccountId: 7,
        amount: 100,
        currency: "INR",
        description: "Test transfer",
        createdAt: "2026-08-17T10:00:00",
      },
    });

    const result = await getTransactionDetails("TXN-TEST-001");

    expect(axiosClient.get).toHaveBeenCalledWith(
      "/api/transactions/TXN-TEST-001",
    );
    expect(result.id).toBe(10);
  });
});
