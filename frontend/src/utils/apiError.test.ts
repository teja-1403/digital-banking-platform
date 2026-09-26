import { describe, expect, it } from "vitest";

import { getApiErrorMessage } from "./apiError";

describe("getApiErrorMessage", () => {
  it("returns the server response message when present", () => {
    const error = {
      isAxiosError: true,
      response: {
        data: {
          message: "Server validation failed",
        },
      },
    };

    expect(getApiErrorMessage(error, "fallback")).toBe(
      "Server validation failed",
    );
  });

  it("falls back to the response error field when message is missing", () => {
    const error = {
      isAxiosError: true,
      response: {
        data: {
          error: "Request rejected by server",
        },
      },
    };

    expect(getApiErrorMessage(error, "fallback")).toBe(
      "Request rejected by server",
    );
  });

  it("returns the fallback for native Error instances", () => {
    expect(getApiErrorMessage(new Error("Network outage"), "fallback")).toBe(
      "fallback",
    );
  });

  it("returns the fallback for unexpected primitive values", () => {
    expect(getApiErrorMessage("bad error", "fallback")).toBe("fallback");
    expect(getApiErrorMessage(null, "fallback")).toBe("fallback");
    expect(getApiErrorMessage(undefined, "fallback")).toBe("fallback");
  });
});
