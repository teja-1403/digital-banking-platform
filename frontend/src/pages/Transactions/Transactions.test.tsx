import { beforeEach, describe, expect, it, vi } from "vitest";

import { render, screen } from "@testing-library/react";

import userEvent from "@testing-library/user-event";

import Transactions from "./Transactions";

vi.mock("../../api/accountApi", () => ({
  getAccounts: vi.fn(),
}));

vi.mock("../../api/transactionApi", () => ({
  getAccountTransactions: vi.fn(),
  getTransactionDetails: vi.fn(),
}));

import { getAccounts } from "../../api/accountApi";

import {
  getAccountTransactions,
  getTransactionDetails,
} from "../../api/transactionApi";

const mockedGetAccounts = vi.mocked(getAccounts);

const mockedGetAccountTransactions = vi.mocked(getAccountTransactions);

const mockedGetTransactionDetails = vi.mocked(getTransactionDetails);

describe("Transactions", () => {
  beforeEach(() => {
    vi.clearAllMocks();

    mockedGetAccounts.mockResolvedValue([
      {
        id: 1,
        accountNumber: "111111111111",
        accountType: "SAVINGS",
        balance: 900,
        currency: "INR",
        status: "ACTIVE",
      },
    ]);

    mockedGetAccountTransactions.mockResolvedValue([
      {
        id: 10,
        transactionReference: "TXN-DETAIL-001",
        idempotencyKey: "idem-detail-001",
        type: "TRANSFER",
        status: "COMPLETED",
        sourceAccountId: 1,
        destinationAccountId: 7,
        amount: 100,
        currency: "INR",
        description: "Test transfer",
        createdAt: "2026-08-17T10:00:00",
        completedAt: "2026-08-17T10:00:01",
      },
    ]);

    mockedGetTransactionDetails.mockResolvedValue({
      id: 10,
      transactionReference: "TXN-DETAIL-001",
      idempotencyKey: "idem-detail-001",
      type: "TRANSFER",
      status: "COMPLETED",
      sourceAccountId: 1,
      destinationAccountId: 7,
      amount: 100,
      currency: "INR",
      description: "Test transfer",
      createdAt: "2026-08-17T10:00:00",
      completedAt: "2026-08-17T10:00:01",
    });
  });

  it("shows an error when transaction details cannot be loaded", async () => {
    const user = userEvent.setup();

    mockedGetTransactionDetails.mockRejectedValue(
      new Error("Transaction details unavailable"),
    );

    render(<Transactions />);

    const transactionReference = await screen.findByText("TXN-DETAIL-001");

    const row = transactionReference.closest("tr");

    expect(row).not.toBeNull();

    await user.click(row!);

    expect(
      await screen.findByText("Unable to load transaction details."),
    ).toBeInTheDocument();
  });
});
