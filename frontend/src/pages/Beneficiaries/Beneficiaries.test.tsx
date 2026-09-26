import { beforeEach, describe, expect, it, vi } from "vitest";

import {
  render,
  screen,
  waitFor,
  waitForElementToBeRemoved,
  within,
} from "@testing-library/react";

import userEvent from "@testing-library/user-event";

import Beneficiaries from "./Beneficiaries";

vi.mock("../../api/beneficiaryApi", () => ({
  getBeneficiaries: vi.fn(),
  createBeneficiary: vi.fn(),
  deleteBeneficiary: vi.fn(),
}));

import {
  createBeneficiary,
  deleteBeneficiary,
  getBeneficiaries,
} from "../../api/beneficiaryApi";

const mockedGetBeneficiaries = vi.mocked(getBeneficiaries);
const mockedCreateBeneficiary = vi.mocked(createBeneficiary);
const mockedDeleteBeneficiary = vi.mocked(deleteBeneficiary);

const getDeleteButton = () =>
  screen.getByTestId("DeleteIcon").closest("button") as HTMLButtonElement;

const beneficiary = {
  id: 1,
  beneficiaryAccountId: 7,
  beneficiaryAccountNumber: "222222222222",
  nickname: "Savings Account",
  createdAt: "2026-08-17T10:00:00",
};

describe("Beneficiaries", () => {
  beforeEach(() => {
    vi.clearAllMocks();

    mockedGetBeneficiaries.mockResolvedValue([beneficiary]);

    mockedCreateBeneficiary.mockResolvedValue({
      id: 2,
      beneficiaryAccountId: 8,
      beneficiaryAccountNumber: "333333333333",
      nickname: "New Beneficiary",
      createdAt: "2026-08-18T10:00:00",
    });

    mockedDeleteBeneficiary.mockResolvedValue(undefined);
  });

  it("loads and displays beneficiaries", async () => {
    render(<Beneficiaries />);

    expect(await screen.findByText("Savings Account")).toBeInTheDocument();

    expect(screen.getByText("222222222222")).toBeInTheDocument();

    expect(screen.getByText(/Account Number/i)).toBeInTheDocument();

    expect(mockedGetBeneficiaries).toHaveBeenCalledTimes(1);
  });

  it("shows empty state when no beneficiaries exist", async () => {
    mockedGetBeneficiaries.mockResolvedValue([]);

    render(<Beneficiaries />);

    expect(
      await screen.findByText(/No beneficiaries yet/i),
    ).toBeInTheDocument();

    expect(
      screen.getAllByRole("button", {
        name: "Add Beneficiary",
      }),
    ).toHaveLength(2);
  });

  it("shows an error when beneficiaries cannot be loaded", async () => {
    mockedGetBeneficiaries.mockRejectedValue(new Error("Network error"));

    render(<Beneficiaries />);

    expect(
      await screen.findByText("Unable to load beneficiaries."),
    ).toBeInTheDocument();
  });

  it("opens the add beneficiary dialog", async () => {
    const user = userEvent.setup();

    render(<Beneficiaries />);

    await screen.findByText("Savings Account");

    await user.click(
      screen.getByRole("button", {
        name: "Add Beneficiary",
      }),
    );

    const dialog = screen.getByRole("dialog");

    expect(
      within(dialog).getByRole("heading", {
        name: "Add Beneficiary",
      }),
    ).toBeInTheDocument();

    expect(
      within(dialog).getByRole("textbox", {
        name: "Beneficiary Account Number",
      }),
    ).toBeInTheDocument();

    expect(
      within(dialog).getByRole("textbox", {
        name: "Nickname",
      }),
    ).toBeInTheDocument();
  });

  it("validates required beneficiary account number", async () => {
    const user = userEvent.setup();

    render(<Beneficiaries />);

    await screen.findByText("Savings Account");

    await user.click(
      screen.getByRole("button", {
        name: "Add Beneficiary",
      }),
    );

    const dialog = screen.getByRole("dialog");

    await user.type(
      within(dialog).getByRole("textbox", {
        name: "Nickname",
      }),
      "Test Beneficiary",
    );

    await user.click(
      within(dialog).getByRole("button", {
        name: "Add Beneficiary",
      }),
    );

    expect(
      await within(dialog).findByText(
        "Beneficiary account number is required.",
      ),
    ).toBeInTheDocument();

    expect(mockedCreateBeneficiary).not.toHaveBeenCalled();
  });

  it("validates required nickname", async () => {
    const user = userEvent.setup();

    render(<Beneficiaries />);

    await screen.findByText("Savings Account");

    await user.click(
      screen.getByRole("button", {
        name: "Add Beneficiary",
      }),
    );

    const dialog = screen.getByRole("dialog");

    await user.type(
      within(dialog).getByRole("textbox", {
        name: "Beneficiary Account Number",
      }),
      "333333333333",
    );

    await user.click(
      within(dialog).getByRole("button", {
        name: "Add Beneficiary",
      }),
    );

    expect(
      await within(dialog).findByText("Nickname is required."),
    ).toBeInTheDocument();

    expect(mockedCreateBeneficiary).not.toHaveBeenCalled();
  });

  it("creates a beneficiary and adds it to the list", async () => {
    const user = userEvent.setup();

    render(<Beneficiaries />);

    await screen.findByText("Savings Account");

    await user.click(
      screen.getByRole("button", {
        name: "Add Beneficiary",
      }),
    );

    const dialog = screen.getByRole("dialog");

    await user.type(
      within(dialog).getByRole("textbox", {
        name: "Beneficiary Account Number",
      }),
      "333333333333",
    );

    await user.type(
      within(dialog).getByRole("textbox", {
        name: "Nickname",
      }),
      "New Beneficiary",
    );

    await user.click(
      within(dialog).getByRole("button", {
        name: "Add Beneficiary",
      }),
    );

    await waitFor(() => {
      expect(mockedCreateBeneficiary).toHaveBeenCalledWith({
        beneficiaryAccountNumber: "333333333333",
        nickname: "New Beneficiary",
      });
    });

    expect(await screen.findByText("New Beneficiary")).toBeInTheDocument();

    expect(screen.getByText("333333333333")).toBeInTheDocument();

    await waitForElementToBeRemoved(dialog);
  });

  it("shows API error when creating a beneficiary fails", async () => {
    mockedCreateBeneficiary.mockRejectedValue(new Error("create failed"));

    const user = userEvent.setup();

    render(<Beneficiaries />);

    await screen.findByText("Savings Account");

    await user.click(
      screen.getByRole("button", {
        name: "Add Beneficiary",
      }),
    );

    const dialog = screen.getByRole("dialog");

    await user.type(
      within(dialog).getByRole("textbox", {
        name: "Beneficiary Account Number",
      }),
      "333333333333",
    );

    await user.type(
      within(dialog).getByRole("textbox", {
        name: "Nickname",
      }),
      "New Beneficiary",
    );

    await user.click(
      within(dialog).getByRole("button", {
        name: "Add Beneficiary",
      }),
    );

    expect(
      await within(dialog).findByText("Unable to add beneficiary."),
    ).toBeInTheDocument();
  });

  it("closes the add dialog when Cancel is clicked", async () => {
    const user = userEvent.setup();

    render(<Beneficiaries />);

    await screen.findByText("Savings Account");

    await user.click(
      screen.getByRole("button", {
        name: "Add Beneficiary",
      }),
    );

    const dialog = screen.getByRole("dialog");

    await user.click(
      within(dialog).getByRole("button", {
        name: "Cancel",
      }),
    );

    await waitForElementToBeRemoved(dialog);
  });

  it("opens delete confirmation dialog", async () => {
    const user = userEvent.setup();

    render(<Beneficiaries />);

    await screen.findByText("Savings Account");

    const deleteButton = getDeleteButton();

    await user.click(deleteButton);

    const dialog = screen.getByRole("dialog");

    expect(
      within(dialog).getByRole("heading", {
        name: "Delete Beneficiary?",
      }),
    ).toBeInTheDocument();

    expect(within(dialog).getByText(/Savings Account/)).toBeInTheDocument();
  });

  it("cancels beneficiary deletion", async () => {
    const user = userEvent.setup();

    render(<Beneficiaries />);

    await screen.findByText("Savings Account");

    const deleteButton = getDeleteButton();

    await user.click(deleteButton);

    const dialog = screen.getByRole("dialog");

    await user.click(
      within(dialog).getByRole("button", {
        name: "Cancel",
      }),
    );

    await waitForElementToBeRemoved(dialog);

    expect(screen.getByText("Savings Account")).toBeInTheDocument();

    expect(mockedDeleteBeneficiary).not.toHaveBeenCalled();
  });

  it("deletes a beneficiary after confirmation", async () => {
    const user = userEvent.setup();

    render(<Beneficiaries />);

    await screen.findByText("Savings Account");

    const deleteButton = getDeleteButton();

    await user.click(deleteButton);

    const dialog = screen.getByRole("dialog");

    expect(
      within(dialog).getByRole("button", {
        name: /^Delete$/i,
      }),
    ).toBeInTheDocument();

    await user.click(
      within(dialog).getByRole("button", {
        name: /^Delete$/i,
      }),
    );

    await waitFor(() => {
      expect(mockedDeleteBeneficiary).toHaveBeenCalledWith(1);
    });

    await waitFor(() => {
      expect(screen.queryByText("Savings Account")).not.toBeInTheDocument();
    });
  });

  it("shows an error when beneficiary deletion fails", async () => {
    const user = userEvent.setup();

    mockedDeleteBeneficiary.mockRejectedValueOnce(
      new Error("Unable to delete beneficiary."),
    );

    render(<Beneficiaries />);

    await screen.findByText("Savings Account");

    const deleteIconButton = getDeleteButton();

    await user.click(deleteIconButton);

    const dialog = await screen.findByRole("dialog");

    expect(
      within(dialog).getByRole("heading", {
        name: "Delete Beneficiary?",
      }),
    ).toBeInTheDocument();

    const confirmDeleteButton = within(dialog).getByRole("button", {
      name: /^delete$/i,
    });

    await user.click(confirmDeleteButton);

    await waitFor(() => {
      expect(mockedDeleteBeneficiary).toHaveBeenCalledWith(1);
    });

    expect(screen.getByRole("dialog")).toBeInTheDocument();
    expect(
      within(screen.getByRole("dialog")).getByRole("heading", {
        name: "Delete Beneficiary?",
      }),
    ).toBeInTheDocument();
    expect(
      within(screen.getByRole("dialog")).getByText("Savings Account"),
    ).toBeInTheDocument();
    expect(
      screen.queryByText(/unable to delete beneficiary/i),
    ).not.toBeInTheDocument();
  });
});
