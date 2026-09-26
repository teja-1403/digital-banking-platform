import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import PageHeader from "./PageHeader";

describe("PageHeader", () => {
  it("renders title, subtitle and optional action", () => {
    render(
      <PageHeader
        title="Beneficiaries"
        subtitle="Manage accounts you can transfer money to."
        action={<button type="button">Add Beneficiary</button>}
      />,
    );

    expect(screen.getByText("Beneficiaries")).toBeInTheDocument();
    expect(
      screen.getByText("Manage accounts you can transfer money to."),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: "Add Beneficiary" }),
    ).toBeInTheDocument();
  });

  it("renders without a subtitle or action when they are omitted", () => {
    render(<PageHeader title="Dashboard" />);

    expect(screen.getByText("Dashboard")).toBeInTheDocument();
    expect(
      screen.queryByText("Manage accounts you can transfer money to."),
    ).not.toBeInTheDocument();
  });
});
