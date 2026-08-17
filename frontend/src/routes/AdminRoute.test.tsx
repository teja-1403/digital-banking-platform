import { describe, expect, it, vi } from "vitest";

import { render, screen } from "@testing-library/react";

import { MemoryRouter, Route, Routes } from "react-router-dom";

import AdminRoute from "./AdminRoute";

vi.mock("../context/AuthContext", () => ({
  useAuth: vi.fn(),
}));

import { useAuth } from "../context/AuthContext";

const mockedUseAuth = vi.mocked(useAuth);

const baseAuth = {
  accessToken: "token",
  isAuthenticated: true,
  isLoading: false,
  login: vi.fn(),
  register: vi.fn(),
  logout: vi.fn(),
};

describe("AdminRoute", () => {
  it("allows ROLE_ADMIN users", () => {
    mockedUseAuth.mockReturnValue({
      ...baseAuth,
      user: {
        userId: 1,
        username: "teja",
        roles: ["ROLE_USER", "ROLE_ADMIN"],
      },
    });

    render(
      <MemoryRouter initialEntries={["/admin/dashboard"]}>
        <Routes>
          <Route element={<AdminRoute />}>
            <Route
              path="/admin/dashboard"
              element={<div>Admin Dashboard</div>}
            />
          </Route>
        </Routes>
      </MemoryRouter>,
    );

    expect(screen.getByText("Admin Dashboard")).toBeInTheDocument();
  });

  it("redirects ROLE_USER to dashboard", () => {
    mockedUseAuth.mockReturnValue({
      ...baseAuth,
      user: {
        userId: 2,
        username: "user2",
        roles: ["ROLE_USER"],
      },
    });

    render(
      <MemoryRouter initialEntries={["/admin/dashboard"]}>
        <Routes>
          <Route element={<AdminRoute />}>
            <Route
              path="/admin/dashboard"
              element={<div>Admin Dashboard</div>}
            />
          </Route>

          <Route path="/dashboard" element={<div>User Dashboard</div>} />
        </Routes>
      </MemoryRouter>,
    );

    expect(screen.getByText("User Dashboard")).toBeInTheDocument();

    expect(screen.queryByText("Admin Dashboard")).not.toBeInTheDocument();
  });

  it("redirects unauthenticated users to login", () => {
    mockedUseAuth.mockReturnValue({
      ...baseAuth,
      user: null,
      accessToken: null,
      isAuthenticated: false,
    });

    render(
      <MemoryRouter initialEntries={["/admin/dashboard"]}>
        <Routes>
          <Route element={<AdminRoute />}>
            <Route
              path="/admin/dashboard"
              element={<div>Admin Dashboard</div>}
            />
          </Route>

          <Route path="/login" element={<div>Login Page</div>} />
        </Routes>
      </MemoryRouter>,
    );

    expect(screen.getByText("Login Page")).toBeInTheDocument();
  });
});
