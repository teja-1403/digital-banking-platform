import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import EmptyState from "./EmptyState";

describe("EmptyState", () => {
  it("renders the title, description and custom action when provided", () => {
    render(
      <EmptyState
        title="No beneficiaries yet"
        description="Add a beneficiary to get started."
        action={<button type="button">Add Beneficiary</button>}
      />,
    );

    expect(screen.getByText("No beneficiaries yet")).toBeInTheDocument();
    expect(
      screen.getByText("Add a beneficiary to get started."),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: "Add Beneficiary" }),
    ).toBeInTheDocument();
  });
});
