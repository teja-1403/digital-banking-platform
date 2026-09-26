import { beforeEach, describe, expect, it, vi } from "vitest";

import { render, screen, waitFor, within } from "@testing-library/react";

import userEvent from "@testing-library/user-event";

import Accounts from "./Accounts";

vi.mock("../../api/accountApi", () => ({
  getAccounts: vi.fn(),
  createAccount: vi.fn(),
  fundAccount: vi.fn(),
}));

vi.mock("../../api/customerApi", () => ({
  getCurrentCustomer: vi.fn(),
}));

vi.mock("./CustomerProfileDialog", () => ({
  default: ({ open, onCreated }: { open: boolean; onCreated: () => void }) =>
    open ? (
      <div data-testid="customer-profile-dialog">
        <button onClick={onCreated}>Create Profile</button>
      </div>
    ) : null,
}));

import { createAccount, fundAccount, getAccounts } from "../../api/accountApi";

import { getCurrentCustomer } from "../../api/customerApi";

const mockedGetAccounts = vi.mocked(getAccounts);
const mockedCreateAccount = vi.mocked(createAccount);
const mockedFundAccount = vi.mocked(fundAccount);
const mockedGetCurrentCustomer = vi.mocked(getCurrentCustomer);

const customer = {
  id: 1,
  userId: 1,
  firstName: "Teja",
  lastName: "Developer",
  phoneNumber: "9876543210",
};

const activeAccount = {
  id: 1,
  accountNumber: "111111111111",
  accountType: "SAVINGS" as const,
  balance: 1000,
  currency: "INR",
  status: "ACTIVE" as const,
};

const blockedAccount = {
  id: 2,
  accountNumber: "222222222222",
  accountType: "CURRENT" as const,
  balance: 500,
  currency: "INR",
  status: "BLOCKED" as const,
};

describe("Accounts", () => {
  beforeEach(() => {
    vi.clearAllMocks();

    mockedGetCurrentCustomer.mockResolvedValue(customer);

    mockedGetAccounts.mockResolvedValue([activeAccount]);

    mockedCreateAccount.mockResolvedValue({
      id: 3,
      accountNumber: "333333333333",
      accountType: "SAVINGS",
      balance: 0,
      currency: "INR",
      status: "ACTIVE",
    });

    mockedFundAccount.mockResolvedValue({
      fundingReference: "FUND-TEST-001",
      accountId: 1,
      accountNumber: "111111111111",
      amount: 500,
      balance: 1500,
      currency: "INR",
    });
  });

  it("loads customer and accounts", async () => {
    render(<Accounts />);

    expect(await screen.findByText("Accounts")).toBeInTheDocument();

    expect(screen.getByText("Customer: Teja Developer")).toBeInTheDocument();

    expect(screen.getByText("111111111111")).toBeInTheDocument();

    expect(screen.getByText("INR 1000.00")).toBeInTheDocument();

    expect(mockedGetCurrentCustomer).toHaveBeenCalledTimes(1);

    expect(mockedGetAccounts).toHaveBeenCalledTimes(1);
  });

  it("shows empty state when customer has no accounts", async () => {
    mockedGetAccounts.mockResolvedValue([]);

    render(<Accounts />);

    expect(await screen.findByText(/No accounts yet/i)).toBeInTheDocument();

    expect(
      screen.getByText(/Open your first banking account to get started/i),
    ).toBeInTheDocument();

    const openAccountButtons = screen.getAllByRole("button", {
      name: "Open Account",
    });

    expect(openAccountButtons).toHaveLength(2);

    expect(openAccountButtons[0]).toBeEnabled();
    expect(openAccountButtons[1]).toBeEnabled();
  });

  it("opens account dialog and creates a new account", async () => {
    const user = userEvent.setup();

    render(<Accounts />);

    await screen.findByText("111111111111");

    const pageOpenAccountButton = screen.getAllByRole("button", {
      name: "Open Account",
    })[0];

    await user.click(pageOpenAccountButton);

    const dialog = screen.getByRole("dialog");

    expect(within(dialog).getByText("Open New Account")).toBeInTheDocument();

    await user.click(
      within(dialog).getByRole("button", {
        name: "Open Account",
      }),
    );

    await waitFor(() => {
      expect(mockedCreateAccount).toHaveBeenCalledWith("SAVINGS");
    });

    expect(screen.getByText("333333333333")).toBeInTheDocument();
  });

  it("allows selecting CURRENT account type", async () => {
    const user = userEvent.setup();

    render(<Accounts />);

    await screen.findByText("111111111111");

    const pageOpenAccountButton = screen.getAllByRole("button", {
      name: "Open Account",
    })[0];

    await user.click(pageOpenAccountButton);

    const dialog = screen.getByRole("dialog");

    const combobox = within(dialog).getByRole("combobox", {
      name: "Account Type",
    });

    await user.click(combobox);

    await user.click(
      screen.getByRole("option", {
        name: "Current",
      }),
    );

    await user.click(
      within(dialog).getByRole("button", {
        name: "Open Account",
      }),
    );

    await waitFor(() => {
      expect(mockedCreateAccount).toHaveBeenCalledWith("CURRENT");
    });
  });

  it("rejects invalid funding amount", async () => {
    const user = userEvent.setup();

    render(<Accounts />);

    await screen.findByText("111111111111");

    await user.click(
      screen.getByRole("button", {
        name: "Fund Account",
      }),
    );

    const dialog = screen.getByRole("dialog");

    await user.type(
      within(dialog).getByRole("textbox", {
        name: "Funding Amount",
      }),
      "0",
    );

    await user.click(
      within(dialog).getByRole("button", {
        name: "Fund Account",
      }),
    );

    expect(
      await within(dialog).findByText(
        "Enter a valid funding amount greater than zero.",
      ),
    ).toBeInTheDocument();

    expect(mockedFundAccount).not.toHaveBeenCalled();
  });

  it("rejects funding amount with more than two decimals", async () => {
    const user = userEvent.setup();

    render(<Accounts />);

    await screen.findByText("111111111111");

    await user.click(
      screen.getByRole("button", {
        name: "Fund Account",
      }),
    );

    const dialog = screen.getByRole("dialog");

    await user.type(
      within(dialog).getByRole("textbox", {
        name: "Funding Amount",
      }),
      "100.123",
    );

    await user.click(
      within(dialog).getByRole("button", {
        name: "Fund Account",
      }),
    );

    expect(
      await within(dialog).findByText(
        "Funding amount can have at most 2 decimal places.",
      ),
    ).toBeInTheDocument();

    expect(mockedFundAccount).not.toHaveBeenCalled();
  });

  it("funds an account and updates the balance", async () => {
    const user = userEvent.setup();

    render(<Accounts />);

    await screen.findByText("INR 1000.00");

    await user.click(
      screen.getByRole("button", {
        name: "Fund Account",
      }),
    );

    const dialog = screen.getByRole("dialog");

    await user.type(
      within(dialog).getByRole("textbox", {
        name: "Funding Amount",
      }),
      "500",
    );

    await user.click(
      within(dialog).getByRole("button", {
        name: "Fund Account",
      }),
    );

    await waitFor(() => {
      expect(mockedFundAccount).toHaveBeenCalledWith(1, 500);
    });

    expect(
      await within(dialog).findByText(
        "Funding successful. Reference: FUND-TEST-001",
      ),
    ).toBeInTheDocument();

    expect(screen.getByText("INR 1500.00")).toBeInTheDocument();
  });

  it("disables funding for a non-active account", async () => {
    mockedGetAccounts.mockResolvedValue([activeAccount, blockedAccount]);

    render(<Accounts />);

    await screen.findByText("222222222222");

    const buttons = screen.getAllByRole("button", {
      name: "Fund Account",
    });

    expect(buttons).toHaveLength(2);

    expect(buttons[0]).toBeEnabled();
    expect(buttons[1]).toBeDisabled();
  });

  it("opens profile dialog when customer profile is missing", async () => {
    mockedGetCurrentCustomer.mockRejectedValue({
      response: {
        status: 404,
      },
    });

    render(<Accounts />);

    expect(
      await screen.findByTestId("customer-profile-dialog"),
    ).toBeInTheDocument();

    expect(screen.getByTestId("customer-profile-dialog")).toBeInTheDocument();
  });

  it("disables Open Account when customer is unavailable", async () => {
    mockedGetCurrentCustomer.mockRejectedValue({
      response: {
        status: 404,
      },
    });

    render(<Accounts />);

    await screen.findByTestId("customer-profile-dialog");

    expect(
      screen.getByRole("button", {
        name: "Open Account",
      }),
    ).toBeDisabled();
  });
});
