import { describe, expect, it } from "vitest";

import {
  getMonthlyTransactionData,
  getTransactionSummary,
} from "./transactionAnalytics";

import type { TransactionResponse } from "../types/transaction";

const baseTransactions: TransactionResponse[] = [
  {
    id: 1,
    transactionReference: "tx-1",
    idempotencyKey: "key-1",
    type: "CREDIT",
    status: "COMPLETED",
    sourceAccountId: 10,
    destinationAccountId: 20,
    amount: 100,
    currency: "INR",
    description: "salary",
    createdAt: "2025-01-15T09:00:00Z",
  },
  {
    id: 2,
    transactionReference: "tx-2",
    idempotencyKey: "key-2",
    type: "DEBIT",
    status: "COMPLETED",
    sourceAccountId: 20,
    destinationAccountId: 10,
    amount: 40,
    currency: "INR",
    description: "rent",
    createdAt: "2025-01-20T10:00:00Z",
  },
  {
    id: 3,
    transactionReference: "tx-3",
    idempotencyKey: "key-3",
    type: "DEBIT",
    status: "FAILED",
    sourceAccountId: 20,
    destinationAccountId: 10,
    amount: 25,
    currency: "INR",
    description: "failed transfer",
    createdAt: "2025-02-02T08:00:00Z",
  },
  {
    id: 4,
    transactionReference: "tx-4",
    idempotencyKey: "key-4",
    type: "CREDIT",
    status: "PENDING",
    sourceAccountId: 10,
    destinationAccountId: 20,
    amount: 500,
    currency: "INR",
    description: "pending credit",
    createdAt: "2025-03-05T12:00:00Z",
  },
  {
    id: 5,
    transactionReference: "tx-5",
    idempotencyKey: "key-5",
    type: "TRANSFER",
    status: "COMPLETED",
    sourceAccountId: 20,
    destinationAccountId: 30,
    amount: -60,
    currency: "INR",
    description: "negative amount",
    createdAt: "2025-03-15T12:00:00Z",
  },
];

describe("transactionAnalytics", () => {
  it("returns zeroed summary for an empty transaction list", () => {
    expect(getTransactionSummary([])).toEqual({
      totalTransactions: 0,
      completedTransactions: 0,
      failedTransactions: 0,
      totalTransferred: 0,
    });
  });

  it("groups completed transactions by month and account direction", () => {
    const result = getMonthlyTransactionData(baseTransactions, 20);

    expect(result).toEqual([
      {
        month: "Jan 2025",
        credits: 100,
        debits: 40,
      },
      {
        month: "Mar 2025",
        credits: 0,
        debits: -60,
      },
    ]);
  });

  it("summarizes totals for completed and failed transactions", () => {
    const summary = getTransactionSummary(baseTransactions);

    expect(summary).toEqual({
      totalTransactions: 5,
      completedTransactions: 3,
      failedTransactions: 1,
      totalTransferred: 80,
    });
  });

  it("handles malformed or unknown transaction data without crashing", () => {
    const malformed: TransactionResponse[] = [
      {
        ...baseTransactions[0],
        status: "UNKNOWN" as never,
        createdAt: "invalid-date",
      },
      {
        ...baseTransactions[1],
        status: "COMPLETED",
        createdAt: "2025-05-01T00:00:00Z",
      },
    ];

    expect(() => getMonthlyTransactionData(malformed, 20)).not.toThrow();
    expect(getTransactionSummary(malformed)).toEqual({
      totalTransactions: 2,
      completedTransactions: 1,
      failedTransactions: 0,
      totalTransferred: 40,
    });
  });
});
