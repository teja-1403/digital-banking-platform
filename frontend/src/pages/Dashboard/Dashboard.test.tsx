import { beforeEach, describe, expect, it, vi } from "vitest";

import { render, screen, waitFor } from "@testing-library/react";

import Dashboard from "./Dashboard";

vi.mock("../../api/accountApi", () => ({
  getAccounts: vi.fn(),
}));

vi.mock("../../api/customerApi", () => ({
  getCurrentCustomer: vi.fn(),
}));

vi.mock("../../api/transactionApi", () => ({
  getAccountTransactions: vi.fn(),
}));

vi.mock("recharts", () => ({
  ResponsiveContainer: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="chart-container">{children}</div>
  ),

  BarChart: ({ children }: { children: React.ReactNode }) => (
    <div data-testid="bar-chart">{children}</div>
  ),

  Bar: () => null,
  CartesianGrid: () => null,
  Legend: () => null,
  Tooltip: () => null,
  XAxis: () => null,
  YAxis: () => null,
}));

import { getAccounts } from "../../api/accountApi";
import { getCurrentCustomer } from "../../api/customerApi";
import { getAccountTransactions } from "../../api/transactionApi";

const mockedGetAccounts = vi.mocked(getAccounts);

const mockedGetCurrentCustomer = vi.mocked(getCurrentCustomer);

const mockedGetAccountTransactions = vi.mocked(getAccountTransactions);

describe("Dashboard", () => {
  beforeEach(() => {
    vi.clearAllMocks();

    mockedGetCurrentCustomer.mockResolvedValue({
      id: 1,
      userId: 1,
      firstName: "Teja",
      lastName: "Developer",
      phoneNumber: "9876543210",
    });

    mockedGetAccounts.mockResolvedValue([
      {
        id: 1,
        accountNumber: "111111111111",
        accountType: "SAVINGS",
        balance: 900,
        currency: "INR",
        status: "ACTIVE",
      },
      {
        id: 2,
        accountNumber: "222222222222",
        accountType: "CURRENT",
        balance: 300,
        currency: "INR",
        status: "ACTIVE",
      },
    ]);

    mockedGetAccountTransactions.mockImplementation(async (accountId) => {
      if (accountId === 1) {
        return [
          {
            id: 10,
            transactionReference: "TXN-001",
            idempotencyKey: "idem-001",
            type: "TRANSFER",
            status: "COMPLETED",
            sourceAccountId: 1,
            destinationAccountId: 7,
            amount: 100,
            currency: "INR",
            description: "Frontend transfer test",
            createdAt: "2026-08-17T10:00:00",
            completedAt: "2026-08-17T10:00:01",
          },
        ];
      }

      return [];
    });
  });

  it("renders customer, total balance and recent transaction", async () => {
    render(<Dashboard />);

    expect(await screen.findByText("Welcome, Teja")).toBeInTheDocument();

    expect(screen.getByText("₹1200.00")).toBeInTheDocument();

    expect(await screen.findByText("TXN-001")).toBeInTheDocument();

    expect(screen.getByText("Frontend transfer test")).toBeInTheDocument();
  });

  it("does not render the analytics chart when no transactions exist", async () => {
    mockedGetAccountTransactions.mockResolvedValue([]);

    render(<Dashboard />);

    await waitFor(() => {
      expect(mockedGetAccountTransactions).toHaveBeenCalled();
    });

    expect(screen.queryByTestId("chart-container")).not.toBeInTheDocument();

    expect(screen.queryByTestId("bar-chart")).not.toBeInTheDocument();
  });

  it("shows failed transaction count", async () => {
    mockedGetAccountTransactions.mockResolvedValue([
      {
        id: 11,
        transactionReference: "TXN-FAILED",
        idempotencyKey: "idem-failed",
        type: "TRANSFER",
        status: "FAILED",
        sourceAccountId: 1,
        destinationAccountId: 7,
        amount: 5000,
        currency: "INR",
        description: "Failed transfer",
        createdAt: "2026-08-17T11:00:00",
      },
    ]);

    render(<Dashboard />);

    const failedLabel = await screen.findByText("Failed");

    const failedCard = failedLabel.closest(".MuiCard-root");

    expect(failedCard).not.toBeNull();

    expect(failedCard!).toHaveTextContent("1");

    expect(screen.getByText("Failed transfer")).toBeInTheDocument();
  });

  it("renders the analytics chart when completed transactions exist", async () => {
    render(<Dashboard />);

    await waitFor(() => {
      expect(screen.getByTestId("chart-container")).toBeInTheDocument();
    });
  });
});
