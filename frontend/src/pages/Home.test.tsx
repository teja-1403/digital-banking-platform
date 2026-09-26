import { render, screen } from "@testing-library/react";
import { describe, expect, it } from "vitest";

import Home from "./Home";

describe("Home", () => {
  it("renders the landing page content", () => {
    render(<Home />);

    expect(screen.getByText("Digital Banking Platform")).toBeInTheDocument();
    expect(
      screen.getByText("Frontend foundation is ready."),
    ).toBeInTheDocument();
  });
});
