import axiosClient from "./axiosClient";
import type { Account } from "../types/account";

export const getAccounts = async (): Promise<Account[]> => {
  const response = await axiosClient.get<Account[]>("/api/accounts");

  return response.data;
};

export const getAccount = async (accountId: number): Promise<Account> => {
  const response = await axiosClient.get<Account>(`/api/accounts/${accountId}`);

  return response.data;
};

export const createAccount = async (
  accountType: "SAVINGS" | "CURRENT",
): Promise<Account> => {
  const response = await axiosClient.post<Account>("/api/accounts", {
    accountType,
  });

  return response.data;
};
