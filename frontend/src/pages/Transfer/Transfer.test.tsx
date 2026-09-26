import { render, screen } from "@testing-library/react";

import userEvent from "@testing-library/user-event";

import { MemoryRouter } from "react-router-dom";

import { beforeEach, describe, expect, it, vi } from "vitest";

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

  it("rejects an amount greater than the balance", async () => {
    const user = userEvent.setup();

    render(
      <MemoryRouter>
        <Transfer />
      </MemoryRouter>,
    );

    await screen.findAllByRole("combobox");

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
