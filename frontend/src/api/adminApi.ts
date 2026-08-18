import axiosClient from "./axiosClient";

import type {
  AdminAccountStats,
  AdminTransactionStats,
  AdminUserStats,
} from "../types/admin";

export const getAdminUserStats = async (): Promise<AdminUserStats> => {
  const response = await axiosClient.get<AdminUserStats>(
    "/api/admin/user-stats",
  );

  return response.data;
};

export const getAdminAccountStats = async (): Promise<AdminAccountStats> => {
  const response = await axiosClient.get<AdminAccountStats>(
    "/api/admin/account-stats",
  );

  return response.data;
};

export const getAdminTransactionStats =
  async (): Promise<AdminTransactionStats> => {
    const response = await axiosClient.get<AdminTransactionStats>(
      "/api/admin/transaction-stats",
    );

    return response.data;
  };
  