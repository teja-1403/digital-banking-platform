import { beforeEach, describe, expect, it, vi } from "vitest";

import { render, screen, waitFor } from "@testing-library/react";

import userEvent from "@testing-library/user-event";

import { MemoryRouter } from "react-router-dom";

import Transfer from "./Transfer";

vi.mock("../../api/accountApi", () => ({
  getAccounts: vi.fn(),
}));

vi.mock("../../api/beneficiaryApi", () => ({
  getBeneficiaries: vi.fn(),
}));

vi.mock("../../api/transactionApi", () => ({
  createTransfer: vi.fn(),
}));

import { getAccounts } from "../../api/accountApi";
import { getBeneficiaries } from "../../api/beneficiaryApi";
import { createTransfer } from "../../api/transactionApi";

const mockedGetAccounts = vi.mocked(getAccounts);

const mockedGetBeneficiaries = vi.mocked(getBeneficiaries);

const mockedCreateTransfer = vi.mocked(createTransfer);

describe("Transfer", () => {
  beforeEach(() => {
    vi.clearAllMocks();

    mockedGetAccounts.mockResolvedValue([
      {
        id: 1,
        accountNumber: "111111111111",
        accountType: "SAVINGS",
        balance: 1000,
        currency: "INR",
        status: "ACTIVE",
      },
    ]);

    mockedGetBeneficiaries.mockResolvedValue([
      {
        id: 2,
        beneficiaryAccountId: 7,
        beneficiaryAccountNumber: "209275150527",
        nickname: "user 2",
      },
    ]);

    mockedCreateTransfer.mockResolvedValue({
      id: 10,
      transactionReference: "TXN-TEST-001",
      idempotencyKey: "test-idempotency-key",
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

  it("sends the selected beneficiary account ID", async () => {
    const user = userEvent.setup();

    render(
      <MemoryRouter>
        <Transfer />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getAllByRole("combobox")).toHaveLength(2);
    });

    const comboboxes = screen.getAllByRole("combobox");

    // 0 = source account
    // 1 = beneficiary
    await user.click(comboboxes[1]);

    const option = await screen.findByRole("option", {
      name: /user 2.*209275150527/i,
    });

    await user.click(option);

    await user.type(
      screen.getByRole("spinbutton", {
        name: /amount/i,
      }),
      "100",
    );

    await user.type(
      screen.getByRole("textbox", {
        name: /description/i,
      }),
      "Test transfer",
    );

    const submitButton = screen.getByRole("button", {
      name: /transfer money/i,
    });

    expect(submitButton).toBeInTheDocument();
    expect(submitButton).not.toBeDisabled();

    await user.click(submitButton);

    await waitFor(() => {
      expect(mockedCreateTransfer).toHaveBeenCalledTimes(1);
    });

    const [request, idempotencyKey] = mockedCreateTransfer.mock.calls[0];

    expect(request).toEqual({
      sourceAccountId: 1,
      destinationAccountId: 7,
      amount: 100,
      currency: "INR",
      description: "Test transfer",
    });

    expect(typeof idempotencyKey).toBe("string");

    expect(idempotencyKey.length).toBeGreaterThan(0);

    /*
     * The component renders:
     * "Reference: TXN-TEST-001"
     * so use a regex instead of exact text matching.
     */
    expect(await screen.findByText(/TXN-TEST-001/i)).toBeInTheDocument();

    expect(screen.getByText(/Status:\s*COMPLETED/i)).toBeInTheDocument();

    expect(screen.getByText(/Amount:\s*INR\s*100\.00/i)).toBeInTheDocument();
  });

  it("rejects an amount greater than the balance", async () => {
    const user = userEvent.setup();

    render(
      <MemoryRouter>
        <Transfer />
      </MemoryRouter>,
    );

    await waitFor(() => {
      expect(screen.getAllByRole("combobox")).toHaveLength(2);
    });

    const comboboxes = screen.getAllByRole("combobox");

    await user.click(comboboxes[1]);

    const option = await screen.findByRole("option", {
      name: /user 2.*209275150527/i,
    });

    await user.click(option);

    await user.type(
      screen.getByRole("spinbutton", {
        name: /amount/i,
      }),
      "2000",
    );

    const submitButton = screen.getByRole("button", {
      name: /transfer money/i,
    });

    expect(submitButton).not.toBeDisabled();

    await user.click(submitButton);

    expect(
      await screen.findByText(/transfer amount exceeds the available balance/i),
    ).toBeInTheDocument();

    expect(mockedCreateTransfer).not.toHaveBeenCalled();
  });
});
