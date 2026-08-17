import { describe, expect, it, vi } from "vitest";

import { render, screen, waitFor } from "@testing-library/react";

import userEvent from "@testing-library/user-event";

import { MemoryRouter } from "react-router-dom";

import Login from "./Login";

vi.mock("../../context/AuthContext", () => ({
  useAuth: vi.fn(),
}));

import { useAuth } from "../../context/AuthContext";

const mockedUseAuth = vi.mocked(useAuth);

describe("Login", () => {
  it("submits username and password", async () => {
    const login = vi.fn().mockResolvedValue(undefined);

    mockedUseAuth.mockReturnValue({
      user: null,
      accessToken: null,
      isAuthenticated: false,
      isLoading: false,
      login,
      register: vi.fn(),
      logout: vi.fn(),
    });

    const user = userEvent.setup();

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>,
    );

    await user.type(
      screen.getByRole("textbox", {
        name: /username/i,
      }),
      "teja",
    );

    await user.type(screen.getByLabelText(/password/i), "Password@123");

    await user.click(
      screen.getByRole("button", {
        name: /sign in/i,
      }),
    );

    await waitFor(() => {
      expect(login).toHaveBeenCalledWith({
        username: "teja",
        password: "Password@123",
      });
    });
  });

  it("displays an error when login fails", async () => {
    const login = vi.fn().mockRejectedValue(new Error("Invalid credentials"));

    mockedUseAuth.mockReturnValue({
      user: null,
      accessToken: null,
      isAuthenticated: false,
      isLoading: false,
      login,
      register: vi.fn(),
      logout: vi.fn(),
    });

    const user = userEvent.setup();

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>,
    );

    await user.type(
      screen.getByRole("textbox", {
        name: /username/i,
      }),
      "wrong",
    );

    await user.type(screen.getByLabelText(/password/i), "wrong-password");

    await user.click(
      screen.getByRole("button", {
        name: /sign in/i,
      }),
    );

    expect(
      await screen.findByText(/invalid username or password/i),
    ).toBeInTheDocument();
  });
});
