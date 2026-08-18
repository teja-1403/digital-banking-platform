import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";

import { authStorage } from "../utils/authStorage";

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;

if (!apiBaseUrl) {
  throw new Error("VITE_API_BASE_URL is not configured.");
}

const axiosClient = axios.create({
  baseURL: apiBaseUrl,
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 10000,
});

const refreshClient = axios.create({
  baseURL: apiBaseUrl,
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 10000,
});

/*
 * Prevent multiple requests from simultaneously
 * refreshing the same access token.
 */
let refreshPromise: Promise<string> | null = null;

const refreshAccessToken = async (): Promise<string> => {
  const refreshToken = authStorage.getRefreshToken();

  if (!refreshToken) {
    throw new Error("No refresh token available.");
  }

  const response = await refreshClient.post<{
    accessToken: string;
    refreshToken?: string;
    tokenType: string;
    expiresIn: number;
    username: string;
    roles: string[];
  }>("/api/auth/refresh", {
    refreshToken,
  });

  const { accessToken, refreshToken: newRefreshToken } = response.data;

  authStorage.setTokens(accessToken, newRefreshToken);

  return accessToken;
};

axiosClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const accessToken = authStorage.getAccessToken();

    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);

axiosClient.interceptors.response.use(
  (response) => response,

  async (error: AxiosError) => {
    const originalRequest = error.config as
      | (InternalAxiosRequestConfig & {
          _retry?: boolean;
        })
      | undefined;

    /*
     * Only handle 401 responses once.
     */
    if (
      error.response?.status !== 401 ||
      !originalRequest ||
      originalRequest._retry
    ) {
      return Promise.reject(error);
    }

    /*
     * Never try to refresh the refresh endpoint itself.
     */
    if (originalRequest.url?.includes("/api/auth/refresh")) {
      authStorage.clear();
      window.location.href = "/login";

      return Promise.reject(error);
    }

    const refreshToken = authStorage.getRefreshToken();

    if (!refreshToken) {
      authStorage.clear();
      window.location.href = "/login";

      return Promise.reject(error);
    }

    originalRequest._retry = true;

    try {
      /*
       * If another request is already refreshing,
       * wait for that same refresh operation.
       */
      if (!refreshPromise) {
        refreshPromise = refreshAccessToken().finally(() => {
          refreshPromise = null;
        });
      }

      const newAccessToken = await refreshPromise;

      originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

      return axiosClient(originalRequest);
    } catch (refreshError) {
      authStorage.clear();

      window.location.href = "/login";

      return Promise.reject(refreshError);
    }
  },
);

export default axiosClient;
