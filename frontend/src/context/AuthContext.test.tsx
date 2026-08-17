import { describe, expect, it, vi, beforeEach } from "vitest";

import { render, screen, waitFor } from "@testing-library/react";

import userEvent from "@testing-library/user-event";

import { AuthProvider, useAuth } from "./AuthContext";

import * as authApi from "../api/authApi";
import { authStorage } from "../utils/authStorage";

vi.mock("../api/authApi", () => ({
  login: vi.fn(),
  register: vi.fn(),
  getCurrentUser: vi.fn(),
  refreshToken: vi.fn(),
  logout: vi.fn(),
}));

function TestComponent() {
  const { user, isAuthenticated, login, logout } = useAuth();

  return (
    <>
      <div>
        authenticated:
        {String(isAuthenticated)}
      </div>

      <div>
        username:
        {user?.username ?? "none"}
      </div>

      <div>
        roles:
        {user?.roles.join(",") ?? "none"}
      </div>

      <button
        onClick={() =>
          void login({
            username: "teja",
            password: "Password@123",
          })
        }
      >
        Login
      </button>

      <button onClick={() => void logout()}>Logout</button>
    </>
  );
}

describe("AuthContext", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();

    vi.mocked(authApi.getCurrentUser).mockResolvedValue({
      userId: 1,
      username: "teja",
      roles: ["ROLE_USER"],
    });
  });

  it("starts unauthenticated when no token exists", async () => {
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>,
    );

    await waitFor(() => {
      expect(screen.getByText("authenticated:false")).toBeInTheDocument();
    });

    expect(screen.getByText("username:none")).toBeInTheDocument();
  });

  it("stores authentication state after login", async () => {
    vi.mocked(authApi.login).mockResolvedValue({
      accessToken: "access-token",
      refreshToken: "refresh-token",
      tokenType: "Bearer",
      expiresIn: 3600,
      username: "teja",
      roles: ["ROLE_USER"],
    });

    const user = userEvent.setup();

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>,
    );

    await user.click(
      screen.getByRole("button", {
        name: "Login",
      }),
    );

    await waitFor(() => {
      expect(screen.getByText("authenticated:true")).toBeInTheDocument();
    });

    expect(screen.getByText("username:teja")).toBeInTheDocument();

    expect(screen.getByText("roles:ROLE_USER")).toBeInTheDocument();

    expect(authStorage.getAccessToken()).toBe("access-token");

    expect(authStorage.getRefreshToken()).toBe("refresh-token");
  });

  it("clears authentication on logout", async () => {
    authStorage.setTokens("access-token", "refresh-token");

    vi.mocked(authApi.getCurrentUser).mockResolvedValue({
      userId: 1,
      username: "teja",
      roles: ["ROLE_USER"],
    });

    vi.mocked(authApi.logout).mockResolvedValue();

    const user = userEvent.setup();

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>,
    );

    await waitFor(() => {
      expect(screen.getByText("authenticated:true")).toBeInTheDocument();
    });

    await user.click(
      screen.getByRole("button", {
        name: "Logout",
      }),
    );

    await waitFor(() => {
      expect(screen.getByText("authenticated:false")).toBeInTheDocument();
    });

    expect(authStorage.getAccessToken()).toBeNull();

    expect(authStorage.getRefreshToken()).toBeNull();
  });
});
