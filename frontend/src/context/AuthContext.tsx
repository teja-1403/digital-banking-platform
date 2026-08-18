import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";

import type {
  AuthResponse,
  AuthUser,
  LoginRequest,
  RegisterRequest,
  RegisterResponse,
} from "../types/auth";

import {
  getCurrentUser,
  login as loginApi,
  logout as logoutApi,
  refreshToken as refreshTokenApi,
  register as registerApi,
} from "../api/authApi";

import { authStorage } from "../utils/authStorage";

interface AuthContextValue {
  user: AuthUser | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;

  login: (request: LoginRequest) => Promise<void>;
  register: (request: RegisterRequest) => Promise<RegisterResponse>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);

  const [accessToken, setAccessToken] = useState<string | null>(
    authStorage.getAccessToken(),
  );

  const [isLoading, setIsLoading] = useState(true);

  const setAuthentication = useCallback((response: AuthResponse) => {
    authStorage.setTokens(response.accessToken, response.refreshToken);

    setAccessToken(response.accessToken);

    setUser({
      userId: 0,
      username: response.username,
      roles: response.roles,
    });
  }, []);

  const login = useCallback(
    async (request: LoginRequest) => {
      const response = await loginApi(request);

      setAuthentication(response);

      /*
       * Fetch the authoritative user details
       * from /api/auth/me.
       */
      const currentUser = await getCurrentUser();

      setUser(currentUser);
    },
    [setAuthentication],
  );

  const register = useCallback(async (request: RegisterRequest) => {
    return registerApi(request);
  }, []);

  const logout = useCallback(async () => {
    const refreshToken = authStorage.getRefreshToken();

    try {
      if (refreshToken) {
        await logoutApi(refreshToken);
      }
    } finally {
      authStorage.clear();
      setAccessToken(null);
      setUser(null);
    }
  }, []);

  useEffect(() => {
    const initializeAuthentication = async () => {
      const existingAccessToken = authStorage.getAccessToken();

      const existingRefreshToken = authStorage.getRefreshToken();

      if (!existingAccessToken) {
        setIsLoading(false);
        return;
      }

      try {
        const currentUser = await getCurrentUser();

        setAccessToken(existingAccessToken);
        setUser(currentUser);
      } catch {
        /*
         * Access token may have expired.
         * Try the refresh token.
         */
        if (existingRefreshToken) {
          try {
            const refreshed = await refreshTokenApi(existingRefreshToken);

            setAuthentication(refreshed);

            const currentUser = await getCurrentUser();

            setUser(currentUser);
          } catch {
            authStorage.clear();
            setAccessToken(null);
            setUser(null);
          }
        } else {
          authStorage.clear();
          setAccessToken(null);
          setUser(null);
        }
      } finally {
        setIsLoading(false);
      }
    };

    void initializeAuthentication();
  }, [setAuthentication]);

  const value = useMemo(
    () => ({
      user,
      accessToken,
      isAuthenticated: Boolean(user && accessToken),
      isLoading,
      login,
      register,
      logout,
    }),
    [user, accessToken, isLoading, login, register, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }

  return context;
}
