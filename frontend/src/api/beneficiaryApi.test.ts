import { beforeEach, describe, expect, it, vi } from "vitest";

import axiosClient from "./axiosClient";

import {
  createBeneficiary,
  deleteBeneficiary,
  getBeneficiaries,
} from "./beneficiaryApi";

vi.mock("./axiosClient", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    delete: vi.fn(),
  },
}));

describe("beneficiaryApi", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("loads beneficiaries from the beneficiaries endpoint", async () => {
    vi.mocked(axiosClient.get).mockResolvedValue({
      data: [
        {
          id: 1,
          beneficiaryAccountId: 7,
          beneficiaryAccountNumber: "222222222222",
          nickname: "Savings Account",
          createdAt: "2026-08-17T10:00:00",
        },
      ],
    });

    const result = await getBeneficiaries();

    expect(axiosClient.get).toHaveBeenCalledWith("/api/beneficiaries");
    expect(result[0].nickname).toBe("Savings Account");
  });

  it("creates a beneficiary with the correct payload", async () => {
    vi.mocked(axiosClient.post).mockResolvedValue({
      data: {
        id: 2,
        beneficiaryAccountId: 8,
        beneficiaryAccountNumber: "333333333333",
        nickname: "New Beneficiary",
        createdAt: "2026-08-18T10:00:00",
      },
    });

    const result = await createBeneficiary({
      beneficiaryAccountNumber: "333333333333",
      nickname: "New Beneficiary",
    });

    expect(axiosClient.post).toHaveBeenCalledWith("/api/beneficiaries", {
      beneficiaryAccountNumber: "333333333333",
      nickname: "New Beneficiary",
    });
    expect(result.nickname).toBe("New Beneficiary");
  });

  it("deletes a beneficiary by id", async () => {
    vi.mocked(axiosClient.delete).mockResolvedValue({ data: undefined });

    await deleteBeneficiary(7);

    expect(axiosClient.delete).toHaveBeenCalledWith("/api/beneficiaries/7");
  });
});
