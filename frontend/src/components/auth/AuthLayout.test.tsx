import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import AuthLayout from "./AuthLayout";

describe("AuthLayout", () => {
  it("renders the public banking branding and the child content", () => {
    render(
      <AuthLayout>
        <div>Login panel</div>
      </AuthLayout>,
    );

    expect(screen.getByText(/welcome to/i)).toBeInTheDocument();
    expect(screen.getByText(/securebank/i)).toBeInTheDocument();
    expect(
      screen.getByText(/bank smarter\. bank securely\./i),
    ).toBeInTheDocument();
    expect(screen.getByText("Login panel")).toBeInTheDocument();
  });
});
