export interface AuthUser {
  userId: number;
  username: string;
  roles: string[];
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken?: string;
  tokenType: string;
  expiresIn: number;
  username: string;
  roles: string[];
}

export interface RegisterResponse {
  id: number;
  username: string;
  email: string;
  roles: string[];
}
