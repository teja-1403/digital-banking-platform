import axiosClient from "./axiosClient";

import type {
  TransactionResponse,
  TransferRequest,
} from "../types/transaction";

export const createTransfer = async (
  request: TransferRequest,
  idempotencyKey: string,
): Promise<TransactionResponse> => {
  const response = await axiosClient.post<TransactionResponse>(
    "/api/transactions/transfers",
    request,
    {
      headers: {
        "Idempotency-Key": idempotencyKey,
      },
    },
  );

  return response.data;
};

export const getAccountTransactions = async (
  accountId: number,
): Promise<TransactionResponse[]> => {
  const response = await axiosClient.get<TransactionResponse[]>(
    `/api/transactions/account/${accountId}`,
  );

  return response.data;
};
