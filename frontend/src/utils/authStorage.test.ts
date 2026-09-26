import { beforeEach, describe, expect, it } from "vitest";

import { authStorage } from "./authStorage";

describe("authStorage", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it("saves and retrieves access and refresh tokens", () => {
    authStorage.setTokens("access-token", "refresh-token");

    expect(authStorage.getAccessToken()).toBe("access-token");
    expect(authStorage.getRefreshToken()).toBe("refresh-token");
  });

  it("clears stored tokens", () => {
    authStorage.setTokens("access-token", "refresh-token");

    authStorage.clear();

    expect(authStorage.getAccessToken()).toBeNull();
    expect(authStorage.getRefreshToken()).toBeNull();
  });

  it("returns null when keys are missing", () => {
    expect(authStorage.getAccessToken()).toBeNull();
    expect(authStorage.getRefreshToken()).toBeNull();
  });

  it("returns stored values even if they are malformed or unexpected strings", () => {
    localStorage.setItem("db_access_token", "{bad-json");
    localStorage.setItem("db_refresh_token", "not-a-token");

    expect(authStorage.getAccessToken()).toBe("{bad-json");
    expect(authStorage.getRefreshToken()).toBe("not-a-token");
  });
});
