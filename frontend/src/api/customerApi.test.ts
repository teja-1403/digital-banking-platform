import { beforeEach, describe, expect, it, vi } from "vitest";

import axiosClient from "./axiosClient";

import { createCustomer, getCurrentCustomer } from "./customerApi";

vi.mock("./axiosClient", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

describe("customerApi", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("fetches the current customer profile", async () => {
    vi.mocked(axiosClient.get).mockResolvedValue({
      data: {
        id: 5,
        firstName: "Teja",
        lastName: "Reddy",
        phoneNumber: "9876543210",
        email: "teja@example.com",
      },
    });

    const result = await getCurrentCustomer();

    expect(axiosClient.get).toHaveBeenCalledWith("/api/customers/me");
    expect(result.firstName).toBe("Teja");
  });

  it("creates a customer with the expected payload", async () => {
    vi.mocked(axiosClient.post).mockResolvedValue({
      data: {
        id: 6,
        firstName: "New",
        lastName: "Customer",
        phoneNumber: "9123456780",
        email: "new@example.com",
      },
    });

    const result = await createCustomer({
      firstName: "New",
      lastName: "Customer",
      phoneNumber: "9123456780",
    });

    expect(axiosClient.post).toHaveBeenCalledWith("/api/customers", {
      firstName: "New",
      lastName: "Customer",
      phoneNumber: "9123456780",
    });
    expect(result.email).toBe("new@example.com");
  });
});
