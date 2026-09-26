import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import StatusChip from "./StatusChip";

describe("StatusChip", () => {
  it("renders common supported statuses", () => {
    const { rerender } = render(<StatusChip status="ACTIVE" />);
    expect(screen.getByText("ACTIVE")).toBeInTheDocument();

    rerender(<StatusChip status="PENDING" />);
    expect(screen.getByText("PENDING")).toBeInTheDocument();

    rerender(<StatusChip status="FAILED" />);
    expect(screen.getByText("FAILED")).toBeInTheDocument();

    rerender(<StatusChip status="COMPLETED" />);
    expect(screen.getByText("COMPLETED")).toBeInTheDocument();
  });

  it("falls back to the default chip style for unknown statuses", () => {
    render(<StatusChip status="UNKNOWN_STATUS" />);

    expect(screen.getByText("UNKNOWN_STATUS")).toBeInTheDocument();
  });
});
