import axiosClient from "./axiosClient";
import type { Account } from "../types/account";

export interface FundAccountResponse {
  fundingReference: string;
  accountId: number;
  accountNumber: string;
  amount: number;
  balance: number;
  currency: string;
}

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

export const fundAccount = async (
  accountId: number,
  amount: number,
): Promise<FundAccountResponse> => {
  const response = await axiosClient.post<FundAccountResponse>(
    `/api/accounts/${accountId}/fund`,
    {
      amount,
    },
  );

  return response.data;
};

export const getFundingStatus = async (): Promise<boolean> => {
  const response = await axiosClient.get<boolean>(
    "/api/accounts/funding-status",
  );

  return response.data;
};
