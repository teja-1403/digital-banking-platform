import { beforeEach, describe, expect, it, vi } from "vitest";

import axiosClient from "./axiosClient";

import {
  getCurrentUser,
  login,
  logout,
  refreshToken,
  register,
} from "./authApi";

vi.mock("./axiosClient", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

describe("authApi", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("logs in with the correct endpoint and payload", async () => {
    vi.mocked(axiosClient.post).mockResolvedValue({
      data: {
        accessToken: "access-token",
        refreshToken: "refresh-token",
        tokenType: "Bearer",
        expiresIn: 3600,
        username: "teja",
        roles: ["ROLE_USER"],
      },
    });

    const result = await login({ username: "teja", password: "Password@123" });

    expect(axiosClient.post).toHaveBeenCalledWith("/api/auth/login", {
      username: "teja",
      password: "Password@123",
    });
    expect(result.username).toBe("teja");
  });

  it("registers a user with the correct request body", async () => {
    vi.mocked(axiosClient.post).mockResolvedValue({
      data: {
        id: 5,
        username: "newuser",
        email: "newuser@example.com",
        roles: ["ROLE_USER"],
      },
    });

    const result = await register({
      username: "newuser",
      email: "newuser@example.com",
      password: "Password@123",
    });

    expect(axiosClient.post).toHaveBeenCalledWith("/api/auth/register", {
      username: "newuser",
      email: "newuser@example.com",
      password: "Password@123",
    });
    expect(result.email).toBe("newuser@example.com");
  });

  it("fetches the current user profile", async () => {
    vi.mocked(axiosClient.get).mockResolvedValue({
      data: {
        userId: 1,
        username: "teja",
        roles: ["ROLE_ADMIN"],
      },
    });

    const result = await getCurrentUser();

    expect(axiosClient.get).toHaveBeenCalledWith("/api/auth/me");
    expect(result.roles).toContain("ROLE_ADMIN");
  });

  it("refreshes the token payload with the stored refresh token", async () => {
    vi.mocked(axiosClient.post).mockResolvedValue({
      data: {
        accessToken: "new-access-token",
        refreshToken: "new-refresh-token",
        tokenType: "Bearer",
        expiresIn: 3600,
        username: "teja",
        roles: ["ROLE_USER"],
      },
    });

    const result = await refreshToken("refresh-token");

    expect(axiosClient.post).toHaveBeenCalledWith("/api/auth/refresh", {
      refreshToken: "refresh-token",
    });
    expect(result.accessToken).toBe("new-access-token");
  });

  it("logs out using the refresh token payload", async () => {
    vi.mocked(axiosClient.post).mockResolvedValue({ data: undefined });

    await logout("refresh-token");

    expect(axiosClient.post).toHaveBeenCalledWith("/api/auth/logout", {
      refreshToken: "refresh-token",
    });
  });
});
