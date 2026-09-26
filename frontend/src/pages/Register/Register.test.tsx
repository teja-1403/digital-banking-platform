import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { beforeEach, describe, expect, it, vi } from "vitest";

import Register from "./Register";

vi.mock("../../context/AuthContext", () => ({
  useAuth: vi.fn(),
}));

import { useAuth } from "../../context/AuthContext";

const mockedUseAuth = vi.mocked(useAuth);

describe("Register", () => {
  beforeEach(() => {
    mockedUseAuth.mockReturnValue({
      user: null,
      accessToken: null,
      isAuthenticated: false,
      isLoading: false,
      login: vi.fn(),
      register: vi.fn().mockResolvedValue({}),
      logout: vi.fn(),
    });
  });

  it("submits the registration form with the entered values", async () => {
    const register = vi.fn().mockResolvedValue({});

    mockedUseAuth.mockReturnValue({
      user: null,
      accessToken: null,
      isAuthenticated: false,
      isLoading: false,
      login: vi.fn(),
      register,
      logout: vi.fn(),
    });

    const user = userEvent.setup();

    render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>,
    );

    await user.type(
      screen.getByRole("textbox", { name: /username/i }),
      "newuser",
    );

    await user.type(
      screen.getByRole("textbox", { name: /email/i }),
      "newuser@example.com",
    );

    await user.type(screen.getByLabelText(/password/i), "Password@123");

    await user.click(screen.getByRole("button", { name: /create account/i }));

    expect(register).toHaveBeenCalledWith({
      username: "newuser",
      email: "newuser@example.com",
      password: "Password@123",
    });
  });

  it("shows a registration error when the request fails", async () => {
    const register = vi.fn().mockRejectedValue(new Error("signup failed"));

    mockedUseAuth.mockReturnValue({
      user: null,
      accessToken: null,
      isAuthenticated: false,
      isLoading: false,
      login: vi.fn(),
      register,
      logout: vi.fn(),
    });

    const user = userEvent.setup();

    render(
      <MemoryRouter>
        <Register />
      </MemoryRouter>,
    );

    await user.type(
      screen.getByRole("textbox", { name: /username/i }),
      "newuser",
    );

    await user.type(
      screen.getByRole("textbox", { name: /email/i }),
      "newuser@example.com",
    );

    await user.type(screen.getByLabelText(/password/i), "Password@123");

    await user.click(screen.getByRole("button", { name: /create account/i }));

    expect(
      await screen.findByText(
        /registration failed. please check your details./i,
      ),
    ).toBeInTheDocument();
  });
});
