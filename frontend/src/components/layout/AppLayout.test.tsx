import { render, screen } from "@testing-library/react";
import { MemoryRouter, Outlet, Route, Routes } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";

import AppLayout from "./AppLayout";

vi.mock("../../context/AuthContext", () => ({
  useAuth: vi.fn(),
}));

import { useAuth } from "../../context/AuthContext";

const mockedUseAuth = vi.mocked(useAuth);

describe("AppLayout", () => {
  beforeEach(() => {
    mockedUseAuth.mockReturnValue({
      user: {
        userId: 1,
        username: "Teja",
        roles: ["ROLE_USER"],
      },
      accessToken: "token-123",
      isAuthenticated: true,
      isLoading: false,
      login: vi.fn(),
      register: vi.fn(),
      logout: vi.fn(),
    });
  });

  it("renders the navigation items and the user summary", () => {
    render(
      <MemoryRouter initialEntries={["/dashboard"]}>
        <Routes>
          <Route element={<AppLayout />}>
            <Route path="/dashboard" element={<div>Dashboard Page</div>} />
          </Route>
        </Routes>
      </MemoryRouter>,
    );

    expect(screen.getAllByText("SecureBank").length).toBeGreaterThan(0);
    expect(screen.getByText("Dashboard")).toBeInTheDocument();
    expect(screen.getByText("Accounts")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /logout/i })).toBeInTheDocument();
    expect(screen.getAllByText("Teja").length).toBeGreaterThan(0);
    expect(screen.getByText("Dashboard Page")).toBeInTheDocument();
  });
});
