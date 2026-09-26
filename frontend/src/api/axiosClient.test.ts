import {
  afterAll,
  beforeAll,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from "vitest";

import type { AxiosInstance } from "axios";

const mocks = vi.hoisted(() => {
  const requestUse = vi.fn();
  const responseUse = vi.fn();

  const mainClient = vi.fn();

  Object.assign(mainClient, {
    interceptors: {
      request: {
        use: requestUse,
      },
      response: {
        use: responseUse,
      },
    },
  });

  const refreshClient = {
    post: vi.fn(),
  };

  const create = vi
    .fn()
    .mockReturnValueOnce(mainClient)
    .mockReturnValueOnce(refreshClient);

  const authStorage = {
    getAccessToken: vi.fn(),
    getRefreshToken: vi.fn(),
    setTokens: vi.fn(),
    clear: vi.fn(),
  };

  return {
    requestUse,
    responseUse,
    mainClient,
    refreshClient,
    create,
    authStorage,
  };
});

vi.mock("axios", () => ({
  default: {
    create: mocks.create,
  },
}));

vi.mock("../utils/authStorage", () => ({
  authStorage: mocks.authStorage,
}));

type TestRequestConfig = {
  url?: string;
  headers: Record<string, string>;
  _retry?: boolean;
};

type TestError = {
  config?: TestRequestConfig;
  response?: {
    status: number;
  };
  message?: string;
};

describe("axiosClient", () => {
  let axiosClient: AxiosInstance;

  let requestFulfilled: (config: TestRequestConfig) => TestRequestConfig;

  let requestRejected: (error: unknown) => Promise<never>;

  let responseFulfilled: (response: unknown) => unknown;

  let responseRejected: (error: TestError) => Promise<unknown>;

  beforeAll(async () => {
    vi.stubEnv("VITE_API_BASE_URL", "http://localhost:8080");

    vi.stubGlobal("window", {
      location: {
        href: "",
      },
    });

    const module = await import("./axiosClient");

    axiosClient = module.default;

    requestFulfilled = mocks.requestUse.mock.calls[0][0];

    requestRejected = mocks.requestUse.mock.calls[0][1];

    responseFulfilled = mocks.responseUse.mock.calls[0][0];

    responseRejected = mocks.responseUse.mock.calls[0][1];
  });

  beforeEach(() => {
    vi.clearAllMocks();

    mocks.authStorage.getAccessToken.mockReturnValue(null);

    mocks.authStorage.getRefreshToken.mockReturnValue(null);

    mocks.refreshClient.post.mockReset();

    mocks.mainClient.mockReset();

    mocks.mainClient.mockResolvedValue({
      data: {},
    });

    window.location.href = "";
  });

  afterAll(() => {
    vi.unstubAllGlobals();
    vi.unstubAllEnvs();
  });

  describe("request interceptor", () => {
    it("adds the access token to the Authorization header", () => {
      mocks.authStorage.getAccessToken.mockReturnValue("access-token-123");

      const config: TestRequestConfig = {
        headers: {},
      };

      const result = requestFulfilled(config);

      expect(mocks.authStorage.getAccessToken).toHaveBeenCalledTimes(1);

      expect(result.headers.Authorization).toBe("Bearer access-token-123");
    });

    it("does not add Authorization when no access token exists", () => {
      mocks.authStorage.getAccessToken.mockReturnValue(null);

      const config: TestRequestConfig = {
        headers: {},
      };

      const result = requestFulfilled(config);

      expect(result.headers.Authorization).toBeUndefined();
    });

    it("preserves the original request config", () => {
      const config: TestRequestConfig = {
        url: "/api/accounts",
        headers: {
          "Content-Type": "application/json",
        },
      };

      const result = requestFulfilled(config);

      expect(result).toBe(config);
    });

    it("rejects request interceptor errors", async () => {
      const error = new Error("Request interceptor failed");

      await expect(requestRejected(error)).rejects.toBe(error);
    });
  });

  describe("response interceptor", () => {
    it("returns successful responses unchanged", () => {
      const response = {
        status: 200,
        data: {
          message: "success",
        },
      };

      const result = responseFulfilled(response);

      expect(result).toBe(response);
    });

    it("rejects non-401 errors without refreshing", async () => {
      const error: TestError = {
        config: {
          url: "/api/accounts",
          headers: {},
        },
        response: {
          status: 500,
        },
      };

      await expect(responseRejected(error)).rejects.toBe(error);

      expect(mocks.refreshClient.post).not.toHaveBeenCalled();

      expect(mocks.authStorage.clear).not.toHaveBeenCalled();

      expect(mocks.mainClient).not.toHaveBeenCalled();
    });

    it("rejects a 401 when there is no request config", async () => {
      const error: TestError = {
        response: {
          status: 401,
        },
      };

      await expect(responseRejected(error)).rejects.toBe(error);

      expect(mocks.refreshClient.post).not.toHaveBeenCalled();
    });

    it("rejects a request that has already been retried", async () => {
      const error: TestError = {
        config: {
          url: "/api/accounts",
          headers: {},
          _retry: true,
        },
        response: {
          status: 401,
        },
      };

      await expect(responseRejected(error)).rejects.toBe(error);

      expect(mocks.refreshClient.post).not.toHaveBeenCalled();

      expect(mocks.mainClient).not.toHaveBeenCalled();
    });

    it("clears authentication when the refresh endpoint itself returns 401", async () => {
      const error: TestError = {
        config: {
          url: "/api/auth/refresh",
          headers: {},
        },
        response: {
          status: 401,
        },
      };

      await expect(responseRejected(error)).rejects.toBe(error);

      expect(mocks.authStorage.clear).toHaveBeenCalledTimes(1);

      expect(window.location.href).toBe("/login");

      expect(mocks.refreshClient.post).not.toHaveBeenCalled();

      expect(mocks.mainClient).not.toHaveBeenCalled();
    });

    it("clears authentication when no refresh token is available", async () => {
      mocks.authStorage.getRefreshToken.mockReturnValue(null);

      const error: TestError = {
        config: {
          url: "/api/accounts",
          headers: {},
        },
        response: {
          status: 401,
        },
      };

      await expect(responseRejected(error)).rejects.toBe(error);

      expect(mocks.authStorage.getRefreshToken).toHaveBeenCalledTimes(1);

      expect(mocks.authStorage.clear).toHaveBeenCalledTimes(1);

      expect(window.location.href).toBe("/login");

      expect(mocks.refreshClient.post).not.toHaveBeenCalled();

      expect(mocks.mainClient).not.toHaveBeenCalled();
    });

    it("refreshes the access token and retries the original request", async () => {
      mocks.authStorage.getRefreshToken.mockReturnValue("refresh-token-123");

      mocks.refreshClient.post.mockResolvedValue({
        data: {
          accessToken: "new-access-token",
          refreshToken: "new-refresh-token",
          tokenType: "Bearer",
          expiresIn: 3600,
          username: "testuser",
          roles: ["USER"],
        },
      });

      const error: TestError = {
        config: {
          url: "/api/accounts",
          headers: {},
        },
        response: {
          status: 401,
        },
      };

      await responseRejected(error);

      expect(mocks.refreshClient.post).toHaveBeenCalledTimes(1);

      expect(mocks.refreshClient.post).toHaveBeenCalledWith(
        "/api/auth/refresh",
        {
          refreshToken: "refresh-token-123",
        },
      );

      expect(mocks.authStorage.setTokens).toHaveBeenCalledWith(
        "new-access-token",
        "new-refresh-token",
      );

      expect(error.config?._retry).toBe(true);

      expect(error.config?.headers.Authorization).toBe(
        "Bearer new-access-token",
      );

      expect(mocks.mainClient).toHaveBeenCalledTimes(1);

      expect(mocks.mainClient).toHaveBeenCalledWith(error.config);

      expect(mocks.authStorage.clear).not.toHaveBeenCalled();
    });

    it("stores undefined when the refresh response does not contain a new refresh token", async () => {
      mocks.authStorage.getRefreshToken.mockReturnValue("refresh-token-123");

      mocks.refreshClient.post.mockResolvedValue({
        data: {
          accessToken: "new-access-token",
          tokenType: "Bearer",
          expiresIn: 3600,
          username: "testuser",
          roles: ["USER"],
        },
      });

      const error: TestError = {
        config: {
          url: "/api/accounts",
          headers: {},
        },
        response: {
          status: 401,
        },
      };

      await responseRejected(error);

      expect(mocks.authStorage.setTokens).toHaveBeenCalledWith(
        "new-access-token",
        undefined,
      );

      expect(error.config?.headers.Authorization).toBe(
        "Bearer new-access-token",
      );
    });

    it("clears authentication when token refresh fails", async () => {
      mocks.authStorage.getRefreshToken.mockReturnValue("refresh-token-123");

      const refreshError = new Error("Refresh request failed");

      mocks.refreshClient.post.mockRejectedValue(refreshError);

      const error: TestError = {
        config: {
          url: "/api/accounts",
          headers: {},
        },
        response: {
          status: 401,
        },
      };

      await expect(responseRejected(error)).rejects.toBe(refreshError);

      expect(mocks.authStorage.clear).toHaveBeenCalledTimes(1);

      expect(window.location.href).toBe("/login");

      expect(mocks.mainClient).not.toHaveBeenCalled();

      expect(mocks.authStorage.setTokens).not.toHaveBeenCalled();
    });

    it("marks the original request as retried before refreshing", async () => {
      mocks.authStorage.getRefreshToken.mockReturnValue("refresh-token-123");

      mocks.refreshClient.post.mockResolvedValue({
        data: {
          accessToken: "new-access-token",
          tokenType: "Bearer",
          expiresIn: 3600,
          username: "testuser",
          roles: ["USER"],
        },
      });

      const originalRequest: TestRequestConfig = {
        url: "/api/accounts",
        headers: {},
      };

      const error: TestError = {
        config: originalRequest,
        response: {
          status: 401,
        },
      };

      await responseRejected(error);

      expect(originalRequest._retry).toBe(true);
    });

    it("does not clear authentication after a successful refresh", async () => {
      mocks.authStorage.getRefreshToken.mockReturnValue("refresh-token-123");

      mocks.refreshClient.post.mockResolvedValue({
        data: {
          accessToken: "new-access-token",
          refreshToken: "new-refresh-token",
          tokenType: "Bearer",
          expiresIn: 3600,
          username: "testuser",
          roles: ["USER"],
        },
      });

      const error: TestError = {
        config: {
          url: "/api/accounts",
          headers: {},
        },
        response: {
          status: 401,
        },
      };

      await responseRejected(error);

      expect(mocks.authStorage.clear).not.toHaveBeenCalled();
    });
  });

  describe("concurrent token refresh", () => {
    it("shares one refresh request between concurrent 401 responses", async () => {
      mocks.authStorage.getRefreshToken.mockReturnValue("refresh-token-123");

      let resolveRefresh: ((value: unknown) => void) | undefined;

      const refreshPromise = new Promise((resolve) => {
        resolveRefresh = resolve;
      });

      mocks.refreshClient.post.mockReturnValue(refreshPromise);

      mocks.mainClient.mockResolvedValue({
        data: {
          success: true,
        },
      });

      const errorOne: TestError = {
        config: {
          url: "/api/accounts",
          headers: {},
        },
        response: {
          status: 401,
        },
      };

      const errorTwo: TestError = {
        config: {
          url: "/api/transactions",
          headers: {},
        },
        response: {
          status: 401,
        },
      };

      const requestOne = responseRejected(errorOne);

      const requestTwo = responseRejected(errorTwo);

      expect(mocks.refreshClient.post).toHaveBeenCalledTimes(1);

      expect(mocks.refreshClient.post).toHaveBeenCalledWith(
        "/api/auth/refresh",
        {
          refreshToken: "refresh-token-123",
        },
      );

      resolveRefresh?.({
        data: {
          accessToken: "shared-access-token",
          refreshToken: "shared-refresh-token",
          tokenType: "Bearer",
          expiresIn: 3600,
          username: "testuser",
          roles: ["USER"],
        },
      });

      await Promise.all([requestOne, requestTwo]);

      expect(mocks.authStorage.setTokens).toHaveBeenCalledTimes(1);

      expect(mocks.authStorage.setTokens).toHaveBeenCalledWith(
        "shared-access-token",
        "shared-refresh-token",
      );

      expect(mocks.mainClient).toHaveBeenCalledTimes(2);

      expect(errorOne.config?.headers.Authorization).toBe(
        "Bearer shared-access-token",
      );

      expect(errorTwo.config?.headers.Authorization).toBe(
        "Bearer shared-access-token",
      );

      expect(errorOne.config?._retry).toBe(true);

      expect(errorTwo.config?._retry).toBe(true);
    });
  });

  describe("refresh token lifecycle", () => {
    it("resets the shared refresh promise after a successful refresh", async () => {
      mocks.authStorage.getRefreshToken.mockReturnValue("refresh-token-123");

      mocks.refreshClient.post.mockResolvedValue({
        data: {
          accessToken: "first-access-token",
          refreshToken: "first-refresh-token",
          tokenType: "Bearer",
          expiresIn: 3600,
          username: "testuser",
          roles: ["USER"],
        },
      });

      const firstError: TestError = {
        config: {
          url: "/api/accounts",
          headers: {},
        },
        response: {
          status: 401,
        },
      };

      await responseRejected(firstError);

      mocks.refreshClient.post.mockResolvedValue({
        data: {
          accessToken: "second-access-token",
          refreshToken: "second-refresh-token",
          tokenType: "Bearer",
          expiresIn: 3600,
          username: "testuser",
          roles: ["USER"],
        },
      });

      const secondError: TestError = {
        config: {
          url: "/api/transactions",
          headers: {},
        },
        response: {
          status: 401,
        },
      };

      await responseRejected(secondError);

      expect(mocks.refreshClient.post).toHaveBeenCalledTimes(2);
    });

    it("resets the shared refresh promise after a failed refresh", async () => {
      mocks.authStorage.getRefreshToken.mockReturnValue("refresh-token-123");

      mocks.refreshClient.post.mockRejectedValueOnce(
        new Error("First refresh failed"),
      );

      const firstError: TestError = {
        config: {
          url: "/api/accounts",
          headers: {},
        },
        response: {
          status: 401,
        },
      };

      await expect(responseRejected(firstError)).rejects.toThrow(
        "First refresh failed",
      );

      mocks.refreshClient.post.mockResolvedValueOnce({
        data: {
          accessToken: "second-access-token",
          refreshToken: "second-refresh-token",
          tokenType: "Bearer",
          expiresIn: 3600,
          username: "testuser",
          roles: ["USER"],
        },
      });

      const secondError: TestError = {
        config: {
          url: "/api/transactions",
          headers: {},
        },
        response: {
          status: 401,
        },
      };

      await responseRejected(secondError);

      expect(mocks.refreshClient.post).toHaveBeenCalledTimes(2);
    });
  });

  it("exports the main axios client instance", () => {
    expect(axiosClient).toBe(mocks.mainClient);
  });
});
