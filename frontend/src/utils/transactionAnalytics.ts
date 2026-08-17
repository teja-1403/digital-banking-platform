import type { TransactionResponse } from "../types/transaction";

export interface TransactionSummary {
  totalTransactions: number;
  completedTransactions: number;
  failedTransactions: number;
  totalTransferred: number;
}

export interface TransactionChartPoint {
  month: string;
  credits: number;
  debits: number;
}

export const getTransactionSummary = (
  transactions: TransactionResponse[],
): TransactionSummary => {
  const completedTransactions = transactions.filter(
    (transaction) => transaction.status === "COMPLETED",
  );

  return {
    totalTransactions: transactions.length,

    completedTransactions: completedTransactions.length,

    failedTransactions: transactions.filter(
      (transaction) => transaction.status === "FAILED",
    ).length,

    totalTransferred: completedTransactions.reduce(
      (total, transaction) => total + transaction.amount,
      0,
    ),
  };
};

export const getMonthlyTransactionData = (
  transactions: TransactionResponse[],
  accountId: number,
): TransactionChartPoint[] => {
  const monthMap = new Map<string, TransactionChartPoint>();

  transactions
    .filter((transaction) => transaction.status === "COMPLETED")
    .forEach((transaction) => {
      const date = new Date(transaction.createdAt);

      const monthKey = `${date.getFullYear()}-${String(
        date.getMonth() + 1,
      ).padStart(2, "0")}`;

      const monthLabel = date.toLocaleDateString("en-IN", {
        month: "short",
        year: "numeric",
      });

      if (!monthMap.has(monthKey)) {
        monthMap.set(monthKey, {
          month: monthLabel,
          credits: 0,
          debits: 0,
        });
      }

      const point = monthMap.get(monthKey)!;

      if (transaction.sourceAccountId === accountId) {
        point.debits += transaction.amount;
      }

      if (transaction.destinationAccountId === accountId) {
        point.credits += transaction.amount;
      }
    });

  return Array.from(monthMap.entries())
    .sort(([a], [b]) => a.localeCompare(b))
    .map(([, value]) => value);
};
