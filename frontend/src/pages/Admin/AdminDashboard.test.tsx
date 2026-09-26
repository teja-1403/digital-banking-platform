import { render, screen } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";

import AdminDashboard from "./AdminDashboard";

vi.mock("../../api/adminApi", () => ({
  getAdminAccountStats: vi.fn(),
  getAdminTransactionStats: vi.fn(),
  getAdminUserStats: vi.fn(),
}));

import {
  getAdminAccountStats,
  getAdminTransactionStats,
  getAdminUserStats,
} from "../../api/adminApi";

const mockedGetAdminAccountStats = vi.mocked(getAdminAccountStats);
const mockedGetAdminTransactionStats = vi.mocked(getAdminTransactionStats);
const mockedGetAdminUserStats = vi.mocked(getAdminUserStats);

describe("AdminDashboard", () => {
  beforeEach(() => {
    vi.clearAllMocks();

    mockedGetAdminUserStats.mockResolvedValue({
      totalUsers: 42,
    });

    mockedGetAdminAccountStats.mockResolvedValue({
      totalCustomers: 18,
      totalAccounts: 31,
      activeAccounts: 27,
      totalBalance: 125000,
    });

    mockedGetAdminTransactionStats.mockResolvedValue({
      totalTransactionVolume: 500000,
      totalTransactions: 90,
      completedTransactions: 74,
      failedTransactions: 5,
    });
  });

  it("renders all admin statistics after loading", async () => {
    render(<AdminDashboard />);

    expect(await screen.findByText("Admin Dashboard")).toBeInTheDocument();
    expect(screen.getByText("Users")).toBeInTheDocument();
    expect(screen.getByText("42")).toBeInTheDocument();
    expect(screen.getByText("₹125000.00")).toBeInTheDocument();
    expect(screen.getByText("Transaction Volume")).toBeInTheDocument();
    expect(screen.getByText("90")).toBeInTheDocument();
  });

  it("shows an error message when the stats request fails", async () => {
    mockedGetAdminUserStats.mockRejectedValue(new Error("Service unavailable"));

    render(<AdminDashboard />);

    expect(
      await screen.findByText("Unable to load admin dashboard data."),
    ).toBeInTheDocument();
  });
});
