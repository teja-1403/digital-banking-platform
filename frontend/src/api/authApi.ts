import axiosClient from "./axiosClient";
import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  RegisterResponse,
} from "../types/auth";

export const login = async (request: LoginRequest): Promise<AuthResponse> => {
  const response = await axiosClient.post<AuthResponse>(
    "/api/auth/login",
    request,
  );

  return response.data;
};

export const register = async (
  request: RegisterRequest,
): Promise<RegisterResponse> => {
  const response = await axiosClient.post<RegisterResponse>(
    "/api/auth/register",
    request,
  );

  return response.data;
};

export const getCurrentUser = async () => {
  const response = await axiosClient.get<AuthUserResponse>("/api/auth/me");

  return response.data;
};

export const refreshToken = async (
  refreshTokenValue: string,
): Promise<AuthResponse> => {
  const response = await axiosClient.post<AuthResponse>("/api/auth/refresh", {
    refreshToken: refreshTokenValue,
  });

  return response.data;
};

export const logout = async (refreshTokenValue: string): Promise<void> => {
  await axiosClient.post("/api/auth/logout", {
    refreshToken: refreshTokenValue,
  });
};

interface AuthUserResponse {
  userId: number;
  username: string;
  roles: string[];
}
